package com.grandp.data.update.command.student;

import com.grandp.data.faculty.Faculty;
import com.grandp.data.update.command.Command;
import com.grandp.data.user.User;
import com.grandp.data.user.student_data.StudentData;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpdateFacultyCommand implements Command {

    private User user;
    private Faculty faculty;

    @Override
    public void execute() {
        StudentData studentData = user.getUserData();

        if (studentData == null) {
            throw new IllegalStateException("User with personal ID: '" + user.getPersonalId() + "' does not have Student Data assigned.");
        }

        user.getUserData().setFaculty(faculty);
    }
}