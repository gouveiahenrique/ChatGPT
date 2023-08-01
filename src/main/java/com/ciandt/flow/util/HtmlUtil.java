package com.ciandt.flow.util;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;

/**
 * @author Wuzi
 */
public class HtmlUtil {

    public static String md2html(String markdown) {
        Parser PARSER = Parser.builder().build();
        Document document = PARSER.parse(markdown);
        HtmlRenderer RENDERER = HtmlRenderer.builder().build();
        return RENDERER.render(document);
    }
}