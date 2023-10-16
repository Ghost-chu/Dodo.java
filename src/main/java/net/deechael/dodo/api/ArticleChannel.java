package net.deechael.dodo.api;

import net.deechael.dodo.types.ArticleObjectType;

public interface ArticleChannel extends Channel {

    String createArticle(String title, String content, String imageUrl);

    void deleteArticleObject(ArticleObjectType type, String id);
}
