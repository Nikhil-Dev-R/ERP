package com.erp.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Date

@Composable
fun FilterScreen0(
    modifier: Modifier = Modifier,
    onApplyFilters: () -> Unit = {}
) {
    val categories = listOf(
        "Class", "Section", "Attendance", "Fees", "Transport", "Library", "Performance"
    )

    val filterOptions = mapOf(
        "Class" to listOf("Nursery", "LKG", "UKG", "1st", "2nd", "3rd", "4th"),
        "Section" to listOf("A", "B", "C"),
        "Attendance" to listOf("Present", "Absent", "Leave"),
        "Fees" to listOf("Paid", "Unpaid", "Pending"),
        "Transport" to listOf("Bus", "Van", "No Transport"),
        "Library" to listOf("Issued", "Returned", "Overdue"),
        "Performance" to listOf("Excellent", "Good", "Average", "Poor")
    )

    val expandedCategory = remember { mutableStateOf<String?>(null) }
    val selectedFilters = remember { mutableStateMapOf<String, MutableSet<String>>() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Filters", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
        }

        categories.forEach { category ->
            item {
                FilterCategory(
                    title = category,
                    options = filterOptions[category].orEmpty(),
                    expanded = expandedCategory.value == category,
                    selectedOptions = selectedFilters[category] ?: mutableSetOf(),
                    onExpandToggle = {
                        expandedCategory.value = if (expandedCategory.value == category) null else category
                    },
                    onOptionSelect = { option ->
                        val currentSet = selectedFilters.getOrPut(category) { mutableSetOf() }
                        if (currentSet.contains(option)) {
                            currentSet.remove(option)
                        } else {
                            currentSet.add(option)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            Button(
                onClick = onApplyFilters,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5722))
            ) {
                Text("Apply Filters", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun FilterCategory(
    title: String,
    options: List<String>,
    expanded: Boolean,
    selectedOptions: Set<String>,
    onExpandToggle: () -> Unit,
    onOptionSelect: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandToggle() }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Expand"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionSelect(option) }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = selectedOptions.contains(option),
                                onCheckedChange = { onOptionSelect(option) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(option)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedFilterCard(
    title: String,
    options: List<String>,
    expanded: Boolean,
    selectedOptions: Set<String>,
    onExpandToggle: () -> Unit,
    onOptionSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onExpandToggle() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Toggle",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    options.forEach { option ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOptionSelect(option) }
                                .padding(top = 8.dp, bottom = 4.dp)
                        ) {
                            Checkbox(
                                checked = selectedOptions.contains(option),
                                onCheckedChange = { onOptionSelect(option) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = option)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterScreen1(
    modifier: Modifier = Modifier,
    onApplyFilters: () -> Unit = {}
) {
    val categories = listOf("Class", "Section", "Attendance", "Fees", "Transport", "Library", "Performance")
    val filterOptions = mapOf(
        "Class" to listOf("Nursery", "LKG", "UKG", "1st", "2nd", "3rd", "4th"),
        "Section" to listOf("A", "B", "C"),
        "Attendance" to listOf("Present", "Absent", "Leave"),
        "Fees" to listOf("Paid", "Unpaid", "Pending"),
        "Transport" to listOf("Bus", "Van", "No Transport"),
        "Library" to listOf("Issued", "Returned", "Overdue"),
        "Performance" to listOf("Excellent", "Good", "Average", "Poor")
    )

    val alphabet = ('A'..'Z').map { it.toString() }
    val selectedLetters = remember { mutableStateListOf<String>() }
    val expandedCategory = remember { mutableStateOf<String?>(null) }
    val selectedFilters = remember { mutableStateMapOf<String, MutableSet<String>>() }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Filters", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Name Starts With", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(8.dp))
                    FlowRow(
                        maxItemsInEachRow = 6,
                    ) {
                        alphabet.forEach { letter ->
                            FilterChip(
                                selected = selectedLetters.contains(letter),
                                onClick = {
                                    if (selectedLetters.contains(letter)) selectedLetters.remove(letter)
                                    else selectedLetters.add(letter)
                                },
                                label = {
                                    Text(letter, fontWeight = FontWeight.Medium)
                                }
                            )
                        }
                    }
                }
            }
        }

        categories.forEach { category ->
            item {
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedCategory.value =
                                        if (expandedCategory.value == category) null else category
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = category,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.widthIn(max = 150.dp)
                            )
                            Icon(
                                imageVector = if (expandedCategory.value == category) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Expand"
                            )
                        }

                        AnimatedVisibility(visible = expandedCategory.value == category) {
                            Column {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                )
                                filterOptions[category].orEmpty().forEach { option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val set =
                                                    selectedFilters.getOrPut(category) { mutableSetOf() }
                                                if (set.contains(option)) set.remove(option) else set.add(
                                                    option
                                                )
                                            }
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Checkbox(
                                            checked = selectedFilters[category]?.contains(option) == true,
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = option)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onApplyFilters,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Apply Filters", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterScreen(
    modifier: Modifier = Modifier,
    onApplyFilters: (
        nameStarts: List<String?>,
        classSet: List<String>,
        sectionSet: List<String>,
        attendanceStatus: String?,
        attendanceStartDate: Date?,
        attendanceEndDate: Date?,
        fees: List<String?>,
        transport: List<String?>,
        library: List<String?>,
        performance: List<String?>
    ) -> Unit
) {

    val alphabet = ('A'..'Z').map { it.toString() }
    val filterOptions = mapOf(
        "Name Starts With" to alphabet,
        "Class" to listOf("Nursery", "LKG", "UKG", "1st", "2nd", "3rd", "4th"),
        "Section" to listOf("A", "B", "C"),
        "Attendance" to listOf("Present", "Absent", "Leave"),
        "Fees" to listOf("Paid", "Unpaid", "Pending"),
        "Transport" to listOf("Bus", "Van", "No Transport"),
        "Library" to listOf("Issued", "Returned", "Overdue"),
        "Performance" to listOf("Excellent", "Good", "Average", "Poor")
    )
    val selectedCategory = remember { mutableStateOf("Class") }
    val selectedFilters = remember { mutableStateMapOf<String, MutableSet<String>>() }
    val selectedLetters = remember { mutableStateListOf<String>() }
    val selectedStartDate = remember { mutableStateOf<Date?>(null) }
    val selectedEndDate = remember { mutableStateOf<Date?>(null) }
    var isDateFilterExpanded by remember { mutableStateOf(selectedCategory.value == "Date") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Filters", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        Row(modifier = Modifier.fillMaxSize()) {
            // Left Column
            Column(
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                filterOptions.keys.forEach { category ->
                    val isSelected = selectedCategory.value == category
                    Text(
                        text = category,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedCategory.value = category }
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.secondary.copy(
                                    alpha = 0.1f
                                ) else Color.Transparent
                            )
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Date",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCategory.value = "Date" }
                        .background(
                            if (isDateFilterExpanded) MaterialTheme.colorScheme.secondary.copy(
                                alpha = 0.1f
                            ) else Color.Transparent
                        )
                        .padding(horizontal = 4.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isDateFilterExpanded) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (isDateFilterExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Right Column
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    AnimatedContent(targetState = selectedCategory.value, label = "") { category ->
                        if (category == "Name Starts With") {
                            FlowRow(
                                maxItemsInEachRow = 6,
                            ) {
                                alphabet.forEach { letter ->
                                    FilterChip(
                                        selected = selectedLetters.contains(letter),
                                        onClick = {
                                            if (selectedLetters.contains(letter)) selectedLetters.remove(letter)
                                            else selectedLetters.add(letter)
                                        },
                                        label = { Text(letter) }
                                    )
                                }
                            }
                        } else {
                            Column {
                                filterOptions[category].orEmpty().forEach { option ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                val currentSet =
                                                    selectedFilters.getOrPut(category) { mutableSetOf() }
                                                if (currentSet.contains(option)) currentSet.remove(
                                                    option
                                                ) else currentSet.add(option)
                                            }
                                            .padding(vertical = 10.dp)
                                    ) {
                                        Checkbox(
                                            checked = selectedFilters[category]?.contains(option) == true,
                                            onCheckedChange = null
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(option)
                                    }
                                    HorizontalDivider()
                                }

                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            onApplyFilters(
                                selectedLetters,
                                selectedFilters["Class"]?.toList().orEmpty(),
                                selectedFilters["Section"]?.toList().orEmpty(),
                                selectedFilters["Attendance"]?.firstOrNull(),
                                null,
                                null,
                                selectedFilters["Fees"]?.toList().orEmpty(),
                                selectedFilters["Transport"]?.toList().orEmpty(),
                                selectedFilters["Library"]?.toList().orEmpty(),
                                selectedFilters["Performance"]?.toList().orEmpty()
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Apply Filters", fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun FilterPreview0() {
//    FilterScreen0()
//}
//
//@Preview
//@Composable
//fun FilterPreview1() {
//    FilterScreen1()
//}

@Preview
@Composable
fun FilterPreview() {
    FilterScreen(
        onApplyFilters = { _, _, _, _, _, _, _, _, _, _ -> }
    )
}