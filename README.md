University Exams & Grading System

Student Name: LATHIKA K
Roll No: 717824I132
Assignment 32: University Exams & Grading – Specification Document
 
Problem Statement:
Design and implement a Java console application for a University Exams & Grading system that manages courses, students, assessments, exam schedules, marks, grades, and transcripts. The application should demonstrate object-oriented principles and maintain consistent evaluation rules.
Class Requirements:
1.	Course
2.	Student
3.	Assessment
4.	ExamSchedule
5.	MarkEntry
6.	Grade
7.	Transcript
Business Rules:
1.	Students can be evaluated only for registered courses.
2.	Marks aggregation follows configured weightages across assessments.
3.	Grades must be derived from total marks using a defined grading policy.
4.	Transcripts include course list, grades, and GPA/CGPA calculations.
5.	Resits or improvements must follow eligibility rules and update records.
Console Interface Requirements:
1.	Menu-driven program: Add Course / Add Student / Create Assessment / Schedule Exam / Record Marks / Publish Grades / Generate Transcript / Exit
2.	Input validations must be performed for all user entries.
3.	Encapsulation must be followed for all attributes.
Expected Output Behavior:
•	Show grade sheets per course and student.
•	Show transcript with term summary and GPA/CGPA.
•	Show assessment-wise analytics (counts, averages).
Questions for Students:
1.	Draw the UML Class Diagram for the above system.
2.	Implement the Classes with necessary Data Members and Methods for System and Business Rules.
3.	Use Aggregation, Inheritance and Polymorphism wherever required.
4.	Implement the main method for Menu Driven System.
1. Create a New Java Project
Open Eclipse.
Go to File → New → Java Project.
Enter a project name, e.g., UniversityGradingSystem.
Click Finish.
2. Create Package Structure
Inside the src folder:
Right-click src → New → Package:
Name: model
Right-click src → New → Package:
Name: service
Right-click src → New → Package:
Name: util
Right-click src → New → Package:
Name: exceptions
Right-click src → New → Package:
Name: main
3. Add Java Classes
For each package, create the corresponding .java files:
model → Person.java, Student.java, Course.java, Assessment.java, ExamSchedule.java, MarkEntry.java, Grade.java, Transcript.java
service → UniversityService.java
util → InputValidator.java
exceptions → InvalidInputException.java
main → Main.java
Copy the code you provided into the respective files.
4. Build the Project
Eclipse automatically compiles Java files.
Make sure there are no red error marks in the Package Explorer.
If errors appear, check for missing imports or typos.
5. Run the Program
Right-click Main.java → Run As → Java Application.
