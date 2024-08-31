Caching: In the computing world, retrieving data from a database can be resource-intensive and time-consuming.   
This is where Redis comes into play. 
Redis helps serve frequently accessed or computed data efficiently, reducing the need to repeatedly query the database.

--------------------------------------

basic types of cache are CLIENT-SIDE CACHE (browser-cache & forward proxy) and SERVER-SIDE CACHE (reverse proxy)

--------------------------------------

Cache Invalidation and Eviction:

Cache INVALIDATION:

Cache can be invalidated after sometime. It will not stay forever.

For example : In instagram, at 10o clock, you could see that you have 500 followers.  In next few mins, some people started following you so that your followers count is increasing.

Initially, the 500 followers is updated in the cache.  but once you noticed people are following, you would like to see how many followers do you have.
Around 50 people started following you in the next few mins or an hour.  you wanted to see the updated followers count.  once it is updated, the old followers count usage is left none.

here invalidation comes into play.  the invalidation can be done over a period of time.  It can be secs, mins, hours, days.

TTL       --> Time To Live 
FIFO      --> First-In First-Out
LRU       --> Least Recently Used
LFU       --> Least Frequently Used
LRFU      --> Least Recently Frequently 
--------------------------------------

Cache EVICTION:


--------------------------------------


There are different types of caches:

1. Cache-aside strategy pattern
2. Read-through strategy pattern
3. Write-through strategy pattern
4. Write-around strategy pattern


