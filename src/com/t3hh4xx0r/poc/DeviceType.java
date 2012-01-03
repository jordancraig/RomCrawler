package com.t3hh4xx0r.poc;

public enum DeviceType {
	   TORO("http://rootzwiki.com/forum/362-cdma-galaxy-nexus-developer-forum");

	    private final String forumUrl;

	    private DeviceType(String forumUrl) {
	        this.forumUrl = forumUrl;
	    }

	    public String getForumUrl() {
	        return forumUrl;
	    }
}
