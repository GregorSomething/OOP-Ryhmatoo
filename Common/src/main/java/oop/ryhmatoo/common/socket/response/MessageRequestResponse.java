package oop.ryhmatoo.common.socket.response;

import oop.ryhmatoo.common.data.Message;

import java.util.List;

/**
 * @param message if error should happen
 */
public record MessageRequestResponse(String message, List<Message> messages) {
}
