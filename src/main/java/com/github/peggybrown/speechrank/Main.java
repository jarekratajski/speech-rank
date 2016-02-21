package com.github.peggybrown.speechrank;import static ratpack.jackson.Jackson.json;import javaslang.collection.List;import com.github.peggybrown.speechrank.entities.Comment;import com.github.peggybrown.speechrank.entities.Conference;import com.github.peggybrown.speechrank.entities.Presentation;import com.github.peggybrown.speechrank.entities.Rate;import ratpack.server.RatpackServer;public class Main {    private static Storage storage;	private static Importer importer = new Importer();	public static void main(String... args) throws Exception {        addFakeConference();        RatpackServer.start(server -> server                .handlers(chain -> chain                        .all(new CORSHandler())						.get("", ctx -> ctx.render(options))                        .post("rating", ctx ->                                ctx.parse(Rate.class).then(rate -> {                                    storage.add(rate);                                    ctx.render(json(rate.getRate()));                                }))                        .post("comment", ctx ->                                ctx.parse(Comment.class).then(comment -> {                                    storage.add(comment);                                    ctx.render(json(comment.getComment()));                                }))                        .post("import", ctx -> {                            importAllConferences();                            ctx.render("OK");                        })                        .get("conferences", ctx -> ctx.render(json(storage.getYears())))                        .get("conference/:id", ctx -> {                            ctx.render(json(storage.getConference(ctx.getPathTokens().get("id"))));                        })));    }	private static void importAllConferences() {		storage.add("2015", new Conference("11", "Confitura", importer.importConfitura2015().map(Presentation::new)));		storage.add("2014", new Conference("12", "Confitura", importer.importConfitura2014().map(Presentation::new)));		storage.add("2015", new Conference("21", "Devoxx", importer.importDevoxxUK2015().map(Presentation::new)));        storage.add("2016", new Conference("22", "Devoxx", List.empty()));	}	private static void addFakeConference() {        storage = new Storage();        storage.addYear("2016");        storage.addYear("2015");        storage.addYear("2014");    }    private static String options =            "GET /conferences\n" +                    "GET /conference/:id\n" +                    "POST /rating\n" +                    "POST /comment\n";}
