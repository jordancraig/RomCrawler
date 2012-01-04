package com.t3hh4xx0r.romcrawler;

public enum DeviceType {
	   TORO("http://rootzwiki.com/forum/362-cdma-galaxy-nexus-developer-forum");
	   ACE("http://rootzwiki.com/forum/163-desire-hd-developer-forum");
	   ERIS("http://rootzwiki.com/forum/34-droid-eris-developer-forum");
	   INC("http://rootzwiki.com/forum/22-droid-incredible-developer-forum");
	   VIVOW("http://rootzwiki.com/forum/60-droid-incredible-2-developer-forum");
	   SHOOTER("http://rootzwiki.com/forum/113-evo-3d-developer-forum");
	   SUPERSONIC("http://rootzwiki.com/forum/36-evo-4g-developer-forum");
	   SPEEDY("http://rootzwiki.com/forum/202-evo-shift-4g-developer-forum");
	   VISION("http://rootzwiki.com/forum/109-g2-vision-developer-forum");
	   DOUBLESHOT("http://rootzwiki.com/forum/142-mytouch-4g-slide-developer-forum");
	   PASSION("http://rootzwiki.com/forum/75-nexus-one-developer-forum");
	   PYRAMID("http://rootzwiki.com/forum/88-sensation-4g-development-forum");
	   MECHA("http://rootzwiki.com/forum/12-thunderbolt-developer-forum");
	   

	   

	    private final String forumUrl;

	    private DeviceType(String forumUrl) {
	        this.forumUrl = forumUrl;
	    }

	    public String getForumUrl() {
	        return forumUrl;
	    }
}
