package net.deechael.dodo;

import net.deechael.dodo.annotation.*;
import net.deechael.dodo.network.Route;

public final class API {

    public static final String BASE_URL = "https://botopen.imdodo.com/api/v2/";

    private static final String POST = "POST";
    private static final String GET = "GET";

    private API() {
    }

    public static class V2 {

        public static class Bot {

            @NonParameters
            @Responses({
                    @Response(name = "clientId", type = String.class),
                    @Response(name = "dodoSourceId", type = String.class),
                    @Response(name = "nickName", type = String.class),
                    @Response(name = "avatarUrl", type = String.class)
            })
            public static Route info() {
                return new Route(POST, "bot/info");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class)
            })
            @NonResponses
            public static Route leave() {
                return new Route(POST, "bot/leave");
            }

            @RequiredParameters({
                    @Parameter(name = "pageSize", type = int.class),
                    @Parameter(name = "maxId", type = long.class)
            })
            @Responses(value = {
                    @Response(name = "maxId", type = Object.class)
            }, extra = {
                    @ListItemResponse(name = "list", content = {
                            @Response(name = "dodoSourceId", type = String.class),
                            @Response(name = "nickName", type = String.class),
                            @Response(name = "avatarUrl", type = String.class)
                    })
            })
            public static Route inviteList() {
                return new Route(POST, "bot/invite/list");
            }

            @RequiredParameters({
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @NonResponses
            public static Route inviteAdd() {
                return new Route(POST, "bot/invite/add");
            }

            @RequiredParameters({
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @NonResponses
            public static Route inviteRemove() {
                return new Route(POST, "bot/invite/remove");
            }

        }

        public static class Island {

            @NonParameters
            @PagedResponses({
                    @Response(name = "islandSourceId", type = String.class),
                    @Response(name = "islandName", type = String.class),
                    @Response(name = "coverUrl", type = String.class),
                    @Response(name = "memberCount", type = int.class),
                    @Response(name = "onlineMemberCount", type = int.class),
                    @Response(name = "defaultChannelId", type = String.class),
                    @Response(name = "systemChannelId", type = String.class)
            })
            public static Route list() {
                return new Route(POST, "island/list");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class)
            })
            @Responses({
                    @Response(name = "islandSourceId", type = String.class),
                    @Response(name = "islandName", type = String.class),
                    @Response(name = "coverUrl", type = String.class),
                    @Response(name = "memberCount", type = int.class),
                    @Response(name = "onlineMemberCount", type = int.class),
                    @Response(name = "defaultChannelId", type = String.class),
                    @Response(name = "systemChannelId", type = String.class)
            })
            public static Route info() {
                return new Route(POST, "island/info");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class)
            })
            @PagedResponses({
                    @Response(name = "dodoSourceId", type = String.class),
                    @Response(name = "nickName", type = String.class),
                    @Response(name = "level", type = int.class),
                    @Response(name = "rank", type = int.class)
            })
            public static Route levelRankList() {
                return new Route(POST, "island/level/rank/list");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "pageSize", type = int.class),
                    @Parameter(name = "maxId", type = long.class)
            })
            @Responses(value = {
                    @Response(name = "maxId", type = Object.class)
            }, extra = {
                    @ListItemResponse(name = "list", content = {
                            @Response(name = "dodoSourceId", type = String.class)
                    })
            })
            public static Route muteList() {
                return new Route(POST, "island/mute/list");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "pageSize", type = int.class),
                    @Parameter(name = "maxId", type = long.class)
            })
            @Responses(value = {
                    @Response(name = "maxId", type = Object.class)
            }, extra = {
                    @ListItemResponse(name = "list", content = {
                            @Response(name = "dodoSourceId", type = String.class)
                    })
            })
            public static Route banList() {
                return new Route(POST, "island/ban/list");
            }

        }

        public static class Channel {

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class)
            })
            @PagedResponses({
                    @Response(name = "channelId", type = String.class),
                    @Response(name = "channelName", type = String.class),
                    @Response(name = "channelType", type = int.class),
                    @Response(name = "defaultFlag", type = int.class),
                    @Response(name = "groupId", type = String.class),
                    @Response(name = "groupName", type = String.class)
            })
            public static Route list() {
                return new Route(POST, "channel/list");
            }

            @RequiredParameters({
                    @Parameter(name = "channelId", type = String.class)
            })
            @PagedResponses({
                    @Response(name = "channelId", type = String.class),
                    @Response(name = "channelName", type = String.class),
                    @Response(name = "channelType", type = int.class),
                    @Response(name = "islandSourceId", type = String.class),
                    @Response(name = "defaultFlag", type = int.class),
                    @Response(name = "groupId", type = String.class),
                    @Response(name = "groupName", type = String.class)
            })
            public static Route info() {
                return new Route(POST, "channel/info");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "channelType", type = int.class, mustBe = "x in (1, 2, 4)")
            })
            @OptionalParameters({
                    @Parameter(name = "channelName", type = String.class)
            })
            @Responses({
                    @Response(name = "channelId", type = String.class),
            })
            public static Route add() {
                return new Route(POST, "channel/add");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "channelId", type = String.class)
            })
            @OptionalParameters({
                    @Parameter(name = "channelName", type = String.class, mustBe = "x.length() <= 32")
            })
            @NonResponses
            public static Route edit() {
                return new Route(POST, "channel/edit");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "channelId", type = String.class)
            })
            @NonResponses
            public static Route remove() {
                return new Route(POST, "channel/remove");
            }

            @RequiredParameters({
                    @Parameter(name = "channelId", type = String.class),
                    @Parameter(name = "messageType", type = int.class, mustBe = "x in [1, 2, 3, 4, 6]"),
                    @Parameter(name = "messageBody", type = Object.class)
            })
            @OptionalParameters({
                    @Parameter(name = "referencedMessageId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @Responses({
                    @Response(name = "messageId", type = String.class),
            })
            public static Route messageSend() {
                return new Route(POST, "channel/message/send");
            }

            @RequiredParameters({
                    @Parameter(name = "messageId", type = String.class),
                    @Parameter(name = "messageType", type = int.class, mustBe = "x in [1, 6]"),
                    @Parameter(name = "messageBody", type = Object.class)
            })
            @Responses({
                    @Response(name = "messageId", type = String.class),
            })
            public static Route messageEdit() {
                return new Route(POST, "channel/message/edit");
            }

            @RequiredParameters({
                    @Parameter(name = "messageId", type = String.class),
                    @Parameter(name = "reason", type = String.class)
            })
            @NonResponses
            public static Route messageWithdraw() {
                return new Route(POST, "channel/message/withdraw");
            }

            @RequiredParameters({
                    @Parameter(name = "messageId", type = String.class),
                    @Parameter(name = "emoji", type = Object.class)
            })
            @NonResponses
            public static Route messageReactionAdd() {
                return new Route(POST, "channel/message/reaction/add");
            }

            @RequiredParameters({
                    @Parameter(name = "messageId", type = String.class),
                    @Parameter(name = "emoji", type = Object.class)
            })
            @OptionalParameters({
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @NonResponses
            public static Route messageReactionRemove() {
                return new Route(POST, "channel/message/reaction/remove");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @Responses({
                    @Response(name = "channelId", type = String.class),
                    @Response(name = "spkStatus", type = int.class),
                    @Response(name = "micStatus", type = int.class),
                    @Response(name = "micSortStatus", type = int.class)
            })
            public static Route voiceMemberStatus() {
                return new Route(POST, "channel/voice/member/status");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "channelId", type = String.class)
            })
            @NonResponses
            public static Route voiceMemberMove() {
                return new Route(POST, "channel/voice/member/move");
            }

            @RequiredParameters({
                    @Parameter(name = "channelId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "operateType", type = int.class)
            })
            @NonResponses
            public static Route voiceMemberEdit() {
                return new Route(POST, "channel/voice/member/edit");
            }


            // TODO article related routes

        }

        public static class Role {

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class)
            })
            @PagedResponses({
                    @Response(name = "roleId", type = String.class),
                    @Response(name = "roleName", type = String.class),
                    @Response(name = "roleColor", type = String.class),
                    @Response(name = "position", type = int.class),
                    @Response(name = "permission", type = String.class)
            })
            public static Route list() {
                return new Route(POST, "role/list");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class)
            })
            @OptionalParameters({
                    @Parameter(name = "roleName", type = String.class),
                    @Parameter(name = "roleColor", type = String.class),
                    @Parameter(name = "position", type = int.class),
                    @Parameter(name = "permission", type = String.class)
            })
            @Responses({
                    @Response(name = "roleId", type = String.class)
            })
            public static Route add() {
                return new Route(POST, "role/add");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "roleId", type = String.class)
            })
            @OptionalParameters({
                    @Parameter(name = "roleName", type = String.class),
                    @Parameter(name = "roleColor", type = String.class),
                    @Parameter(name = "position", type = int.class),
                    @Parameter(name = "permission", type = String.class)
            })
            @NonResponses
            public static Route edit() {
                return new Route(POST, "role/edit");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "roleId", type = String.class)
            })
            @NonResponses
            public static Route remove() {
                return new Route(POST, "role/remove");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "roleId", type = String.class)
            })
            @NonResponses
            public static Route memberAdd() {
                return new Route(POST, "role/member/add");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "roleId", type = String.class)
            })
            @NonResponses
            public static Route memberRemove() {
                return new Route(POST, "role/member/remove");
            }

        }

        public static class Member {

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "pageSize", type = int.class),
                    @Parameter(name = "maxId", type = long.class)
            })
            @Responses(value = {
                    @Response(name = "maxId", type = Object.class)
            }, extra = {
                    @ListItemResponse(name = "list", content = {
                            @Response(name = "dodoSourceId", type = String.class),
                            @Response(name = "nickName", type = String.class),
                            @Response(name = "personalNickName", type = String.class),
                            @Response(name = "AvatarUrl", type = String.class),
                            @Response(name = "joinTime", type = String.class),
                            @Response(name = "sex", type = int.class),
                            @Response(name = "level", type = int.class),
                            @Response(name = "isBot", type = int.class),
                            @Response(name = "onlineDevice", type = int.class),
                            @Response(name = "onlineStatus", type = int.class)
                    })
            })
            public static Route list() {
                return new Route(POST, "member/list");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @Responses({
                    @Response(name = "islandSourceId", type = String.class),
                    @Response(name = "dodoSourceId", type = String.class),
                    @Response(name = "nickName", type = String.class),
                    @Response(name = "personalNickName", type = String.class),
                    @Response(name = "AvatarUrl", type = String.class),
                    @Response(name = "joinTime", type = String.class),
                    @Response(name = "sex", type = int.class),
                    @Response(name = "level", type = int.class),
                    @Response(name = "isBot", type = int.class),
                    @Response(name = "onlineDevice", type = int.class),
                    @Response(name = "onlineStatus", type = int.class)
            })
            public static Route info() {
                return new Route(POST, "member/info");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @PagedResponses({
                    @Response(name = "roleId", type = String.class),
                    @Response(name = "roleName", type = String.class),
                    @Response(name = "roleColor", type = String.class),
                    @Response(name = "position", type = int.class),
                    @Response(name = "permission", type = String.class)
            })
            public static Route roleList() {
                return new Route(POST, "member/role/list");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @Responses({
                    @Response(name = "dodoSourceId", type = String.class),
                    @Response(name = "nickName", type = String.class),
                    @Response(name = "invitationCount", type = int.class)
            })
            public static Route invitationInfo() {
                return new Route(POST, "member/invitation/info");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "nickName", type = String.class, mustBe = "len(x) < 32")
            })
            @NonResponses
            public static Route nicknameEdit() {
                return new Route(POST, "member/nickname/edit");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "duration", type = int.class, description = "second")
            })
            @OptionalParameters({
                    @Parameter(name = "reason", type = String.class, mustBe = "len(x) < 64")
            })
            @NonResponses
            public static Route muteAdd() {
                return new Route(POST, "member/mute/add");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @NonResponses
            public static Route muteRemove() {
                return new Route(POST, "member/mute/remove");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @OptionalParameters({
                    @Parameter(name = "noticeChannelId", type = String.class),
                    @Parameter(name = "reason", type = String.class, mustBe = "len(x) < 64")
            })
            @NonResponses
            public static Route banAdd() {
                return new Route(POST, "member/ban/add");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @NonResponses
            public static Route banRemove() {
                return new Route(POST, "member/ban/remove");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "platform", type = String.class)
            })
            @OptionalParameters({
                    @Parameter(name = "issuer", type = String.class, description = "Required when \"series\" is filled"),
                    @Parameter(name = "series", type = String.class)
            })
            @Responses({
                    @Response(name = "isBandPlatform", type = int.class),
                    @Response(name = "isHaveIssuer", type = int.class),
                    @Response(name = "isHaveSeries", type = int.class)
            })
            public static Route nftStatus() {
                return new Route(POST, "member/nft/status");
            }

        }

        public static class Personal {

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "messageType", type = int.class, mustBe = "x in [1, 2, 3]"),
                    @Parameter(name = "messageBody", type = String.class)
            })
            @Responses({
                    @Response(name = "messageId", type = String.class)
            })
            public static Route messageSend() {
                return new Route(POST, "personal/message/send");
            }

        }

        public static class Integral {
            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class)
            })
            @Responses({
                    @Response(name = "integralBalance", type = Long.class)
            })
            public static Route getIntegralInfo() {
                return new Route(POST, "integral/info");
            }

            @RequiredParameters({
                    @Parameter(name = "islandSourceId", type = String.class),
                    @Parameter(name = "dodoSourceId", type = String.class),
                    @Parameter(name = "operateType", type = Integer.class),
                    @Parameter(name = "integral", type = Long.class)
            })
            public static Route setIntegralEdit() {
                return new Route(POST, "integral/edit");
            }
        }

        public static class Resource {

            @RequiredParameters({
                    @Parameter(name = "file", type = byte[].class, description = "binary")
            })
            @Responses({
                    @Response(name = "url", type = String.class),
                    @Response(name = "width", type = int.class),
                    @Response(name = "height", type = int.class)
            })
            public static Route pictureUpload() {
                return new Route(POST, "resource/picture/upload");
            }

        }

        public static class Websocket {

            @NonParameters
            @Responses({
                    @Response(name = "endpoint", type = String.class)
            })
            public static Route connection() {
                return new Route(POST, "websocket/connection");
            }

        }

    }

}