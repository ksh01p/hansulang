package com.example.demo.service.impl;

import com.example.demo.domain.Menu;
import com.example.demo.domain.Review;
import com.example.demo.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReviewAverageScoreCalculationTest {

    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository = Mockito.mock(ReviewRepository.class);
    }

    private double calculateAverage(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0.0;
        return reviews.stream()
                .mapToInt(Review::getScore)
                .average()
                .orElse(0.0);
    }

    @Test
    void testAverageScore_MultipleReviews() {
        List<Review> reviews = Arrays.asList(
                createReview(4),
                createReview(5),
                createReview(3)
        );

        Mockito.when(reviewRepository.findByMenuId(1L)).thenReturn(reviews);
        List<Review> fetched = reviewRepository.findByMenuId(1L);

        double average = calculateAverage(fetched);
        assertEquals(4.0, average, 0.01);
    }

    @Test
    void testAverageScore_SingleReview() {
        List<Review> reviews = Collections.singletonList(createReview(5));

        Mockito.when(reviewRepository.findByMenuId(2L)).thenReturn(reviews);
        double average = calculateAverage(reviewRepository.findByMenuId(2L));

        assertEquals(5.0, average);
    }

    @Test
    void testAverageScore_NoReviews() {
        Mockito.when(reviewRepository.findByMenuId(3L)).thenReturn(Collections.emptyList());
        double average = calculateAverage(reviewRepository.findByMenuId(3L));

        assertEquals(0.0, average);
    }

    @Test
    void testAverageScore_ZeroScores() {
        List<Review> reviews = Arrays.asList(
                createReview(0),
                createReview(0),
                createReview(0)
        );

        Mockito.when(reviewRepository.findByMenuId(4L)).thenReturn(reviews);
        double average = calculateAverage(reviewRepository.findByMenuId(4L));

        assertEquals(0.0, average);
    }

    @Test
    void testAverageScore_FloatingResult() {
        List<Review> reviews = Arrays.asList(
                createReview(3),
                createReview(4),
                createReview(5)
        );

        Mockito.when(reviewRepository.findByMenuId(5L)).thenReturn(reviews);
        double average = calculateAverage(reviewRepository.findByMenuId(5L));

        assertEquals(4.0, average, 0.01);
    }

    private Review createReview(int score) {
        Review r = new Review();
        r.setScore(score);
        return r;
    }
}
