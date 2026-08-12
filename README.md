<div align="center">
  <img
    src="./screenshots/presencify_logo.png"
    alt="Presencify Logo"
    width="120"
    height="120"
    style="border-radius: 15px;"
  />
</div>
<h1>Presencify</h1>
<p>
  A secure, cross-platform attendance ecosystem that combines dynamic QR codes,
  on-device face recognition with OpenCV, and interactive liveness verification
  to prevent proxy attendance and digital spoofing. Beyond attendance tracking,
  it provides timetable-based upcoming class schedules for students and teachers,
  notifies students about extra lectures, and allows teachers to share attendance
  details with students' parents in a single click via WhatsApp.
</p>

> **Note:** Presencify consists of **76 screens** in total. Only selected screens and workflows are shown below for demonstration purposes.

---

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white) ![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=apple&logoColor=white) ![Desktop](https://img.shields.io/badge/Desktop-333333?style=for-the-badge&logo=computer&logoColor=white)


## 👥 Available Roles & Features Overview

Presencify operates on a role-based access control (RBAC) architecture with three primary user roles tailored for academic administration and daily classroom workflows:

### 🛡️ Administrator
* **Academic Hierarchy Management**: Complete CRUD (Create, Read, Update, Delete) operations for Universities, Branches, Schemes, Semesters, Courses, Classrooms, Divisions, and Batches.
* **User Onboarding & Management**: Bulk onboarding of students and teachers via CSV file imports or manual entry, with dedicated search and multi-criteria filtering tools.
* **Biometric & Face Enrollment**: Review, approve, or reject biometric face profile uploads submitted by students.
* **Academic Status Tracking**: Manage student dropouts, re-assign academic divisions, and maintain structural integrity across courses.
* **Analytics & Defaulter Audits**: Monitor campus-wide aggregate attendance graphs and export semester/course defaulter lists directly to CSV format.

---

### 👨‍🏫 Teacher
* **Dynamic QR Attendance**: Initiate attendance sessions by displaying dynamic QR codes embedded with 1-second NTP-synced time payloads.
* **Manual Attendance Marking**: Access real-time student lists to manually toggle present/absent statuses.
* **Session & Schedule Management**: View upcoming class schedules, timetables, and add extra lectures on the fly.
* **Flexible Course Adjustments**: View, edit, or add attendance for assigned courses or cover proxy lectures when colleagues are absent.
* **Reporting & Parent Communication**: Generate detailed course-wise defaulter lists (viewable in-app or exported as CSV) and instantly share 1-click app-generated attendance summaries (with full present and absent student details) directly to parents and departmental groups via WhatsApp.
---

### 🎓 Student
* **Secure Attendance Verification**: Scan live dynamic QR codes and pass randomized, anti-spoofing facial liveness challenges directly on-device to confirm physical presence.
* **Self-Monitoring Analytics**: View personalized aggregate attendance graphs, course-by-course percentage breakdowns, and granular per-lecture history.
* **Personalized Schedule & Timetable**: Access daily class schedules, course timetables, and read-only curriculum details.
* **Real-time FCM Notifications**: Receive instant Firebase push notifications regarding extra lectures, schedule modifications, or class updates.
* **Profile & Biometrics**: Review profile details and track biometric registration status.

---
## 🎓 Student Features

The student portal is engineered for seamless daily attendance tracking and complete transparency over academic standings. Utilizing edge processing via OpenCV and Google ML Kit, students can securely mark attendance in seconds while maintaining strict data privacy.

### 🔑 Authentication & Dynamic Schedule
* **Student Login**: Secure login routing users to their specific student dashboard and permissions.
* **View Timetable**: Real-time schedule integration displaying daily lectures, timings, classrooms, and assigned faculty.

| Student Login | View Timetable |
| :---: | :---: |
| <img src="./screenshots/login_screen_student.png" width="300" /> | <img src="./screenshots/view_timetable.png" width="300" /> |

<br>

### 📊 Attendance Analytics
* **Self-Monitoring Analytics**: Visual progress rings and weekly trends tracking overall attendance percentages per enrolled course.
* **Recently Marked Attendances**: Displays the student's most recently marked attendance records across courses.

| Attendance Dashboard | Attendance Dashboard |
| :---: | :---: |
| <img src="./screenshots/student_attendance_dashboard_first_half.png" width="300" /> | <img src="./screenshots/student_attendance_dashboard_second_half.png" width="300" /> |

<br>

### 📅 Upcoming Sessions & Course History
* **Upcoming Sessions Feed**: Chronological feed of upcoming lectures, room allocations, and real-time status updates.
* **Per-Lecture Audit Trail**: Granular history for individual courses displaying exact date, time, faculty name, and Present/Absent status.

| Upcoming Classes | Attendance History (Specific Course) |
| :---: | :---: |
| <img src="./screenshots/upcoming_classes_student.png" width="300" /> | <img src="./screenshots/attendance_history_of_a_student_for_specific_course.png" width="300" /> |

<br>

### 👤 Profile Details & Biometric Status
* **Academic Credentials**: Displays PRN, department, branch, semester, division, and assigned batch information.
* **Biometric Registration Status**: Tracks the approval state of local face descriptors uploaded for identity verification.

| Profile Details | Profile Details |
| :---: | :---: |
| <img src="./screenshots/student_profile_details_first_half.png" width="300" /> | <img src="./screenshots/student_profile_details_second_half.png" width="300" /> |

---

### 🔒 Smart Attendance & Anti-Spoofing Verification

To enforce physical presence and eliminate proxy attendance (buddy punching), Presencify combines **1-second NTP-synced Dynamic QR codes** with an interactive **Google ML Kit & OpenCV facial verification flow**.

* **Multi-Modal Verification Workflow**: Scans time-sensitive dynamic QR, executes randomized liveness prompts, and verifies face recognition locally using OpenCV.
* **Randomized Challenge Engine**: Powered by Google ML Kit to track real-time head poses (yaw/pitch). If a student performs an unprompted action or plays a video, the sequence immediately invalidates to prevent spoofing.
* **Continuous Frame Tracking**: Real-time face detection monitors presence in the camera frame. If the face leaves the frame even for a second, an error triggers and resets the challenge sequence.

| Marking Attendance (Scan QR + Liveness) | Behavioral Anti-Spoofing Check | Absence Detection Safeguard |
| :---: | :---: | :---: |
| <img src="./screenshots/student_marking_attendance_with_scan_qr_liveness.gif" width="250" /> | <img src="./screenshots/liveness_detection_fails_if_student_performs_the_wrong_action_this_prevents_if_student_is_showing_video_to_camera.png" width="250" /> | <img src="./screenshots/liveness_detection_shows_error_if_no_face_in_frame_even_for_a_second.png" width="250" /> |

<br>

### 🔔 Real-Time Extra Class Notifications

* **Instant Push Alerts (FCM)**: Real-time Firebase Cloud Messaging notifications alerting students to extra lectures or schedule updates, directing them straight to the class details screen.

| Opening Extra Class Notification |
| :---: |
| <img src="./screenshots/student_opening_notification_of_extra_lecture.gif" width="300" /> |

---

## 👨‍🏫 Teacher Features

The teacher dashboard provides faculty with complete control over lecture management, multi-modal attendance marking (manual or dynamic QR) & automated defaulters tracking. Allows teachers to export the current lecture's attendance details, including present and absent student lists, as text and share them directly with students' parents through WhatsApp groups.

### 📅 Schedule Tracking & Attendance Audit
* **Upcoming Classes**: Real-time schedule integration displaying daily assigned lectures, room allocations, course details, and active time slots.
* **View Marked Attendances**: Historical logs allowing teachers to review, modify, or verify previously recorded attendance sessions.

| Upcoming Classes |                       View Already Marked Attendances                       |
| :---: |:---------------------------------------------------------------------------:|
| <img src="./screenshots/teacher_upcoming_clases.png" width="300" /> | <img src="./screenshots/view_already_marked_attendances.png" width="350" /> |

<br>

### 📋 Defaulters Tracking & CSV Reporting
* **In-App Defaulters List**: Automated calculation identifying students falling below official attendance thresholds across specific courses or semesters.
* **CSV Data Export**: One-click export functionality generating structured, Excel-compatible CSV reports for departmental audits and institutional records.

| Defaulters List in App |                                 Defaulters List Exported as CSV                                 |
| :---: |:-----------------------------------------------------------------------------------------------:|
| <img src="./screenshots/defaulters_list_in_app.png" width="300" /> | <img src="./screenshots/defaulters_list_exported_as_csv_and_opened_in_excel.png" width="600" /> |

<br>

### ⚙️ Class & Attendance Session Setup
* **Class Details**: Displays complete details of a lecture based on the timetable, including the course, schedule, and other relevant class information. It also provides an option to mark attendance for that course.
* **Create Attendance Session**: Allows teachers to select the date for which they want to record attendance before proceeding with the attendance marking process.

| Class Details | Create Attendance |
| :---: | :---: |
| <img src="./screenshots/class_details_with_mark_attendance_option.png" width="300" /> | <img src="./screenshots/create_attendance.png" width="300" /> |

<br>

### ⏱️ Multi-Modal Marking Mechanisms
* **Manual Attendance Marking**: Interactive roster allowing quick manual toggles for present/absent statuses.
* **Dynamic QR Generation**: Displays live, 1-second refreshing dynamic QR codes backed by NTP time-synchronization to prevent screenshot sharing.

|                       Mark Attendance Manually                       | Mark Attendance with Dynamic QR |
|:--------------------------------------------------------------------:| :---: |
| <img src="./screenshots/mark_attendance_manually.png" width="325" /> | <img src="./screenshots/mark_attendance_with_dynamic_qr.png" width="300" /> |

<br>

### 💬 Parent Communication & Group Reporting
* **1-Click WhatsApp Report Sharing**: Automatically generates formatted lecture summaries—including complete present and absent student lists—and opens WhatsApp to send instant updates directly to parents or departmental faculty groups.

| Sharing Attendance via WhatsApp |
| :---: |
| <img src="./screenshots/attendance_sharing_to_wtp.gif" width="300" /> |

---

## 🛡️ Admin Features

The administrator portal offers centralized oversight over the entire institutional ecosystem, enabling complete management of user roles, academic structures, biometric enrollments, and campus-wide attendance analytics.

### 👤 Profile & Institutional Analytics
* **Admin Profile**: Central administrative profile and security credentials dashboard.
* **Aggregate Attendance Graphs**: Campus-wide data visualization rendering interactive graphs to analyze overall attendance trends per semester, division, and course.

| Admin Profile |                             Graph Analysis (All Students in Semester)                              |
| :---: |:--------------------------------------------------------------------------------------------------:|
| <img src="./screenshots/admin_profile.png" width="300" /> | <img src="./screenshots/attendance_graph_analysis_of_all_students_in_semester_.png" width="550" /> |

<br>

### 👥 User Management & Bulk Onboarding
* **CSV Bulk Import**: High-performance onboarding engine allowing admins to import thousands of student records simultaneously via structured CSV files.
* **User Role Management**: Dedicated tab to view, edit, filter, and manage accounts and permissions for both students and teachers across departments.

|                         Import Students via CSV                         | Manage Users (Students & Teachers) |
|:-----------------------------------------------------------------------:| :---: |
| <img src="./screenshots/import_students_through_csv.png" width="500" /> | <img src="./screenshots/users_tab_for_admin_to_manage_students_and_teachers.png" width="300" /> |

<br>

### 🏛️ Academic Structure & Biometric Approval
* **Academic Hierarchy Management**: Complete CRUD operations for configuring Universities, Branches, Schemes, Semesters, Courses, Classrooms, Divisions, and Batches.
* **Biometric Enrollment Approval**: Gateway allowing administrators to review, approve, or reject facial descriptors submitted by students during onboarding.

| Manage Academics | Approve/Reject Uploaded Biometrics |
| :---: | :---: |
| <img src="./screenshots/manage_academics_tab.png" width="300" /> | <img src="./screenshots/approve_or_rejectbiometrics_uploaded_by_tudent.png" width="300" /> |

<br>

### 🔍 Global Search & Filtering Systems
* **Student Search & Filters**: Fast lookup tool equipped with multi-criteria filters (Branch, Division, Batch, Roll No, PRN) to quickly locate student records.
* **Teacher & Semester Search**: Granular search interfaces for locating faculty accounts, active semesters, and course mappings.

| Search Students | Search Student Filter Option |
| :---: | :---: |
| <img src="./screenshots/search_students.png" width="300" /> | <img src="./screenshots/search_student_filter_option.png" width="300" /> |

| Search Teachers | Search Semesters |
| :---: | :---: |
| <img src="./screenshots/search_teachers.png" width="300" /> | <img src="./screenshots/search_semesters.png" width="300" /> |

<br>
