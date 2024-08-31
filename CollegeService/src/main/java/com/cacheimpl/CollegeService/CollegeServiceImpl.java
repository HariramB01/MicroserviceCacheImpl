package com.cacheimpl.CollegeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CollegeServiceImpl implements CollegeService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String COLLEGE_CACHE_KEY_PREFIX = "college:";
    private static final String COLLEGE_CACHE_KEY_ALL = COLLEGE_CACHE_KEY_PREFIX + "all";

    private final CollegeRepository collegeRepository;

    public CollegeServiceImpl(CollegeRepository collegeRepository) {
        this.collegeRepository = collegeRepository;
    }

    private final CacheStrategy<List<College>, String> allCollegesCacheStrategy = new CacheStrategy<>() {
        @Override
        public Optional<List<College>> get(String key) {
            return Optional.ofNullable((List<College>) redisTemplate.opsForValue().get(COLLEGE_CACHE_KEY_ALL));
        }

        @Override
        public void set(String key, List<College> value, long timeout, TimeUnit timeUnit) {
            redisTemplate.opsForValue().set(COLLEGE_CACHE_KEY_ALL, value, timeout, timeUnit);
        }

        @Override
        public void delete(String key) {
            redisTemplate.delete(COLLEGE_CACHE_KEY_ALL);
        }
    };

    private final CacheStrategy<College, Long> collegeByIdCacheStrategy = new CacheStrategy<>() {
        @Override
        public Optional<College> get(Long key) {
            return Optional.ofNullable((College) redisTemplate.opsForValue().get(COLLEGE_CACHE_KEY_PREFIX + key));
        }

        @Override
        public void set(Long key, College value, long timeout, TimeUnit timeUnit) {
            redisTemplate.opsForValue().set(COLLEGE_CACHE_KEY_PREFIX + key, value, timeout, timeUnit);
        }

        @Override
        public void delete(Long key) {
            redisTemplate.delete(COLLEGE_CACHE_KEY_PREFIX + key);
        }
    };

    @Override
    public List<College> getAllColleges() {
        // Fetch data from cache using the strategy
        Optional<List<College>> cachedColleges = allCollegesCacheStrategy.get(null);

        // Check if the cache is empty or null
        if (cachedColleges.isEmpty()) {
            System.out.println("Fetching data from db");

            // Fetch data from database
            List<College> colleges = collegeRepository.findAll();

            // Update cache with fetched data
            allCollegesCacheStrategy.set(null, colleges, 5, TimeUnit.MINUTES);

            return colleges;
        }

        // If data is present in cache
        System.out.println("Fetching data from cache");
        return cachedColleges.get();
    }

    @Override
    public College createCollege(College college) {
        // Save to the database
        return collegeRepository.save(college);
    }

    @Override
    public Optional<College> getCollegeById(Long id) {
        // Fetch from cache using the strategy
        Optional<College> cachedCollege = collegeByIdCacheStrategy.get(id);
        if (cachedCollege.isPresent()) {
            System.out.println("Returning college from cache for ID: " + id);
            return cachedCollege;
        }

        // Fetch from the database
        System.out.println("Fetching college from DB for ID: " + id);
        Optional<College> college = collegeRepository.findById(id);

        // Cache if found
        college.ifPresent(c -> collegeByIdCacheStrategy.set(id, c, 1, TimeUnit.MINUTES));
        return college;
    }

    @Override
    public College updateCollege(Long id, College college) {
        Optional<College> existingCollege = collegeRepository.findById(id);
        if (existingCollege.isPresent()) {
            College updatedCollege = existingCollege.get();
            updatedCollege.setName(college.getName());
            updatedCollege.setLocation(college.getLocation());
            collegeRepository.save(updatedCollege);

            System.out.println("Updating data in the DB");

            // Delete cache entries
            collegeByIdCacheStrategy.delete(id);
            allCollegesCacheStrategy.delete(null);

            return updatedCollege;
        }
        return null;
    }

    // CacheStrategy interface definition
    private interface CacheStrategy<T, ID> {
        Optional<T> get(ID id);

        void set(ID id, T value, long timeout, TimeUnit timeUnit);

        void delete(ID id);
    }
}
