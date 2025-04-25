package com.nextstep.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextstep.data.model.Task
import com.nextstep.data.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class TaskCalendarViewModel @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    private val _tasksForSelectedDate = MutableStateFlow<List<Task>>(emptyList())
    val tasksForSelectedDate: StateFlow<List<Task>> = _tasksForSelectedDate

    private val _taskCountByDate = MutableStateFlow<Map<LocalDate, Int>>(emptyMap())
    val taskCountByDate: StateFlow<Map<LocalDate, Int>> = _taskCountByDate

    init {
        // 当选择的日期改变时，加载该日期的任务
        viewModelScope.launch {
            selectedDate
                .collect { date ->
                    _isLoading.value = true
                    val tasks = taskRepository.getTasksByDate(date)
                    _tasksForSelectedDate.value = tasks
                    _isLoading.value = false
                }
        }

        // 当月份改变时，加载该月任务数量统计
        viewModelScope.launch {
            currentMonth
                .collect { month ->
                    loadTaskCountsForMonth(month)
                }
        }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        
        // 如果选择的日期不在当前月份，更新当前月份
        val newMonth = YearMonth.of(date.year, date.month)
        if (newMonth != _currentMonth.value) {
            _currentMonth.value = newMonth
        }
    }

    fun nextMonth() {
        _currentMonth.value = _currentMonth.value.plusMonths(1)
    }

    fun previousMonth() {
        _currentMonth.value = _currentMonth.value.minusMonths(1)
    }

    private suspend fun loadTaskCountsForMonth(month: YearMonth) {
        _isLoading.value = true
        val firstDay = month.atDay(1)
        val lastDay = month.atEndOfMonth()
        
        val taskCountsMap = taskRepository.getTaskCountInDateRange(firstDay, lastDay)
        
        // 将Map<Long, Int>转换为Map<LocalDate, Int>
        val convertedMap = taskCountsMap.map { (timestamp, count) ->
            // 将时间戳转换为LocalDate
            val date = LocalDate.ofEpochDay(timestamp / (24 * 60 * 60 * 1000))
            date to count
        }.toMap()
        
        _taskCountByDate.value = convertedMap
        _isLoading.value = false
    }
} 