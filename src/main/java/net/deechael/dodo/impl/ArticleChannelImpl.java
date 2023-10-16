package net.deechael.dodo.impl;

import com.google.gson.JsonObject;
import net.deechael.dodo.API;
import net.deechael.dodo.api.ArticleChannel;
import net.deechael.dodo.gate.Gateway;
import net.deechael.dodo.network.Route;
import net.deechael.dodo.types.ArticleObjectType;

public class ArticleChannelImpl extends ChannelImpl implements ArticleChannel {

    public ArticleChannelImpl(Gateway gateway, JsonObject info) {
        super(gateway, info);
    }


    @Override
    public String createArticle(String title, String content, String imageUrl) {
        Route route = API.V2.Channel.createArticle()
                .param("channelId", getId())
                .param("title", title)
                .param("content", content)
                .param("imageUrl", imageUrl);
        return gateway.executeRequest(route).getAsJsonObject().get("articleId").getAsString();
    }

    @Override
    public void deleteArticleObject(ArticleObjectType type, String id) {
        Route route = API.V2.Channel.deleteArticleObject()
                .param("channelId", getId())
                .param("type", type)
                .param("id", id);
         gateway.executeRequest(route);
    }
}
