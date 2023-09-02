package com.grandp.data.command.update.request;

import com.grandp.data.entity.authority.SimpleAuthority;
import com.grandp.data.command.Command;
import com.grandp.data.command.update.user.*;
import com.grandp.data.entity.user.User;
import com.grandp.data.exception.CommandCannotBeExecutedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class UpdateUserRequest implements UpdateRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserRequest.class);

    private User user;
    private String newFirstName;
    private String newLastName;
    private String newEmail;
    private String newPersonalId;
    private Set<SimpleAuthority> authoritiesToAdd;
    private Set<SimpleAuthority> authoritiesToRemove;

    private List<Command> commands = new LinkedList<>();

    public UpdateUserRequest(User user,
                             String newFirstName,
                             String newLastName,
                             String newPersonalId,
                             String newEmail,
                             Set<SimpleAuthority> authoritiesToAdd,
                             Set<SimpleAuthority> authoritiesToRemove) {
        if (user == null) {
            throw new IllegalArgumentException("Cannot create UpdateUserRequest. Given User is null.");
        }

        this.user = user;
        this.newFirstName = newFirstName;
        this.newLastName = newLastName;
        this.newEmail = newEmail;
        this.newPersonalId = newPersonalId;
        this.authoritiesToAdd = authoritiesToAdd;
        this.authoritiesToRemove = authoritiesToRemove;
    }

    @Override
    public void execute() {
        load();

        for (Command c : commands) {
            try {
                c.execute();
            } catch (CommandCannotBeExecutedException e) {
                LOGGER.info("Command " + c.getClass() + " cannot be executed. " + e.getMessage() + ". Start reverting of commands.");
                revert();
            }
        }
    }

    @Override
    public void revert() {
        for (Command c : commands) {
            c.revert();
        }
    }

    private void load() {
        if (newFirstName != null && newFirstName.length() > 0) {
            commands.add(new UpdateUserFirstNameCommand(user, newFirstName));
        }

        if (newLastName != null && newLastName.length() > 0) {
            commands.add(new UpdateUserLastNameCommand(user, newLastName));
        }

        if (newEmail != null && newEmail.length() > 0) {
            commands.add(new UpdateUserEmailCommand(user, newEmail));
        }

        if (newPersonalId != null && newPersonalId.length() > 0) {
            commands.add(new UpdateUserPersonalIdCommand(user, newPersonalId));
        }

        if (authoritiesToAdd != null && authoritiesToRemove != null) {
            commands.add(new UpdateUserAuthoritiesCommand(user, authoritiesToAdd, authoritiesToRemove));
        }
    }
}
