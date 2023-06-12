package com.grandp.data.command.update.user;

import com.grandp.data.entity.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.undo.CannotUndoException;

public class UpdateUserPersonalIdCommand extends UpdateUserCommandHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserPersonalIdCommand.class);

    private User user;
    private String newPersonalId;
    private String oldPersonalId;

    public UpdateUserPersonalIdCommand(User user, String newPersonalId) {
        super(user);
        this.user = user;
        this.newPersonalId = newPersonalId;
    }

    @Override
    public void execute() {
        oldPersonalId = user.getPersonalId();
        user.setFirstName(newPersonalId);
        executed = true;
        LOGGER.info("Changed PersonalID for User with Personal ID: '" + user.getPersonalId() + "' from '" + oldPersonalId + "' to '" + newPersonalId + "'.");
    }

    @Override
    public void revert() throws CannotUndoException {
        if (executed) {
            user.setPersonalId(oldPersonalId);
            LOGGER.info("Reverted PersonalID for User with Personal ID: '" + user.getPersonalId() + "' back to '" + oldPersonalId + "'.");
        } else {
            LOGGER.info("No revert of PersonalID required.");
        }
    }
}