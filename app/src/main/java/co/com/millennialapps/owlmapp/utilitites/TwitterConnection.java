package co.com.millennialapps.owlmapp.utilitites;

import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterConnection {
    private static TwitterConnection instance;

    private TwitterConnection() {
    }

    public static TwitterConnection getInstance () {
        if (instance  == null) {
            instance = new TwitterConnection();
        }
        return instance;
    }
    private ConfigurationBuilder getConfBuilder () {
        ConfigurationBuilder  cb = new ConfigurationBuilder ();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("MqNKNubJSJmgm7Q0QS90K7PzT")
                .setOAuthConsumerSecret("JecH0wIdalWYXDn3ceVpkqaanrddayMtDftJAG1OYERD7W6ool")
                .setOAuthAccessToken("100571062-F7uuIpqO9HRJSoi7gwc6HI1Bq0TyD4HaBQ9kzqCy")
                .setOAuthAccessTokenSecret("K7eYTjQbiFGwWiKZPtphdzJnHL4vYu93AVWEW0UlZaP3k");
        return cb;
    }
    public void getTimelineFeedInBackground(TwitterListener  listener) {
        AsyncTwitterFactory  factory = new AsyncTwitterFactory (( getConfBuilder().build()));
        AsyncTwitter  async = factory.getInstance ();
        async.addListener(listener);
        async.getUserTimeline("un_bogota");
    }
}
