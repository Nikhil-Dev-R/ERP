package com.erp.modules.academics.data

import com.erp.modules.academics.data.model.ClassRoom
import com.erp.modules.academics.data.model.Subject
import com.erp.modules.academics.data.model.TimeTableEntry

/**
 * Sample data for testing the Academics module
 */
object SampleData {
    
    /**
     * Sample subjects for different grade levels
     */
    val sampleSubjects = listOf(
        // Class 1 subjects
        Subject(
            id = "sub_c1_math",
            name = "Mathematics",
            code = "MATH101",
            description = "Foundational mathematics for young learners",
            gradeLevel = "Class 1",
            credits = 4,
            isElective = false,
            syllabus = """
                Unit 1: Numbers 1-100
                - Counting and writing numbers
                - Number patterns and sequences
                
                Unit 2: Addition and Subtraction
                - Single digit addition
                - Single digit subtraction
                - Word problems
                
                Unit 3: Shapes and Patterns
                - Basic 2D shapes
                - Pattern recognition
                
                Unit 4: Measurements
                - Length (longer/shorter)
                - Weight (heavier/lighter)
                - Time (hours, days, months)
            """.trimIndent()
        ),
        Subject(
            id = "sub_c1_eng",
            name = "English",
            code = "ENG101",
            description = "Basic English language skills",
            gradeLevel = "Class 1",
            credits = 4,
            isElective = false,
            syllabus = """
                Unit 1: Alphabets
                - Recognition of all 26 letters
                - Matching uppercase and lowercase letters
                
                Unit 2: Phonics
                - Basic phonetic sounds
                - Blending sounds to form words
                
                Unit 3: Reading
                - Sight words
                - Simple sentences
                - Short stories with pictures
                
                Unit 4: Writing
                - Forming letters correctly
                - Writing simple words
                - Basic punctuation
            """.trimIndent()
        ),
        
        // Class 5 subjects
        Subject(
            id = "sub_c5_math",
            name = "Mathematics",
            code = "MATH501",
            description = "Intermediate mathematics for Class 5 students",
            gradeLevel = "Class 5",
            credits = 4,
            isElective = false,
            syllabus = """
                Unit 1: Number System
                - Large numbers up to millions
                - Place value and face value
                - Fractions and decimals
                
                Unit 2: Operations
                - Addition, subtraction, multiplication and division of large numbers
                - BODMAS rule
                - Word problems
                
                Unit 3: Geometry
                - Angles and their measurement
                - Triangles and their properties
                - Perimeter and area of simple shapes
                
                Unit 4: Data Handling
                - Collection and organization of data
                - Bar graphs and pie charts
                - Mean, median and mode
            """.trimIndent()
        ),
        Subject(
            id = "sub_c5_sci",
            name = "Science",
            code = "SCI501",
            description = "General science covering natural phenomena and basic scientific concepts",
            gradeLevel = "Class 5",
            credits = 4,
            isElective = false,
            syllabus = """
                Unit 1: Living World
                - Plant and animal kingdoms
                - Habitats and adaptations
                - Food chains and webs
                
                Unit 2: Human Body
                - Major organ systems
                - Nutrition and health
                - Diseases and prevention
                
                Unit 3: Matter and Materials
                - States of matter
                - Properties of materials
                - Changes (physical and chemical)
                
                Unit 4: Earth and Space
                - Solar system
                - Weather and climate
                - Natural resources and conservation
            """.trimIndent()
        ),
        
        // Class 10 subjects
        Subject(
            id = "sub_c10_math",
            name = "Mathematics",
            code = "MATH1001",
            description = "Advanced mathematics for secondary school students",
            gradeLevel = "Class 10",
            credits = 5,
            isElective = false,
            syllabus = """
                Unit 1: Algebra
                - Quadratic equations
                - Polynomials
                - Arithmetic progressions
                
                Unit 2: Geometry
                - Triangles (similarity and congruence)
                - Circles (theorems and properties)
                - Coordinate geometry
                
                Unit 3: Trigonometry
                - Trigonometric ratios
                - Heights and distances
                - Applications
                
                Unit 4: Statistics and Probability
                - Measures of central tendency
                - Cumulative frequency distributions
                - Probability of events
                
                Unit 5: Surface Areas and Volumes
                - 3D shapes
                - Combination of solids
                - Applications
            """.trimIndent()
        ),
        Subject(
            id = "sub_c10_sci",
            name = "Science",
            code = "SCI1001",
            description = "Comprehensive science course covering physics, chemistry and biology",
            gradeLevel = "Class 10",
            credits = 5,
            isElective = false,
            syllabus = """
                Physics:
                - Light (reflection, refraction)
                - Electricity and magnetism
                - Force, work and energy
                
                Chemistry:
                - Chemical reactions
                - Acids, bases and salts
                - Metals and non-metals
                - Carbon and its compounds
                
                Biology:
                - Life processes
                - Control and coordination
                - Reproduction
                - Heredity and evolution
            """.trimIndent()
        ),
        Subject(
            id = "sub_c10_comp",
            name = "Computer Science",
            code = "CS1001",
            description = "Introduction to computer science and programming",
            gradeLevel = "Class 10",
            credits = 3,
            isElective = true,
            syllabus = """
                Unit 1: Basics of Computing
                - Computer hardware and software
                - Operating systems
                - Networking concepts
                
                Unit 2: Data Representation
                - Number systems
                - Data types
                - Boolean logic
                
                Unit 3: Programming Fundamentals
                - Algorithms and flowcharts
                - Introduction to programming languages
                - Basic programming concepts
                
                Unit 4: Web Technologies
                - HTML and CSS basics
                - Website structure
                - Internet security and ethics
            """.trimIndent()
        )
    )
    
    /**
     * Sample classrooms/sections
     */
    val sampleClassRooms = listOf(
        ClassRoom(
            id = "class_1a",
            name = "Class 1",
            section = "A",
            roomNumber = "101",
            capacity = 30,
            academicYear = "2023-2024"
        ),
        ClassRoom(
            id = "class_1b",
            name = "Class 1",
            section = "B",
            roomNumber = "102",
            capacity = 30,
            academicYear = "2023-2024"
        ),
        ClassRoom(
            id = "class_5a",
            name = "Class 5",
            section = "A",
            roomNumber = "201",
            capacity = 35,
            academicYear = "2023-2024"
        ),
        ClassRoom(
            id = "class_5b",
            name = "Class 5",
            section = "B",
            roomNumber = "202",
            capacity = 35,
            academicYear = "2023-2024"
        ),
        ClassRoom(
            id = "class_10a",
            name = "Class 10",
            section = "A",
            roomNumber = "301",
            capacity = 40,
            academicYear = "2023-2024"
        ),
        ClassRoom(
            id = "class_10b",
            name = "Class 10",
            section = "B",
            roomNumber = "302",
            capacity = 40,
            academicYear = "2023-2024"
        )
    )
    
    /**
     * Sample timetable entries for Class 5A
     */
    val sampleTimetableEntries = listOf(
        // Monday timetable for Class 5A
        TimeTableEntry(
            id = "tt_5a_mon_1",
            classRoomId = "class_5a",
            subjectId = "sub_c5_math",
            dayOfWeek = 1, // Monday
            periodNumber = 1,
            startTime = "08:00",
            endTime = "08:45",
            roomId = "201"
        ),
        TimeTableEntry(
            id = "tt_5a_mon_2",
            classRoomId = "class_5a",
            subjectId = "sub_c5_sci",
            dayOfWeek = 1, // Monday
            periodNumber = 2,
            startTime = "08:50",
            endTime = "09:35",
            roomId = "201"
        ),
        
        // Tuesday timetable for Class 5A
        TimeTableEntry(
            id = "tt_5a_tue_1",
            classRoomId = "class_5a",
            subjectId = "sub_c5_sci",
            dayOfWeek = 2, // Tuesday
            periodNumber = 1,
            startTime = "08:00",
            endTime = "08:45",
            roomId = "201"
        ),
        TimeTableEntry(
            id = "tt_5a_tue_2",
            classRoomId = "class_5a",
            subjectId = "sub_c5_math",
            dayOfWeek = 2, // Tuesday
            periodNumber = 2,
            startTime = "08:50",
            endTime = "09:35",
            roomId = "201"
        )
    )
} 