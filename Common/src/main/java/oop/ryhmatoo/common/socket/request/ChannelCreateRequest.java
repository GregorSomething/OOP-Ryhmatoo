package oop.ryhmatoo.common.socket.request;

import oop.ryhmatoo.common.data.Channel;

import java.util.List;

public record ChannelCreateRequest(String name, Channel.Type type, List<String> members) {
}
