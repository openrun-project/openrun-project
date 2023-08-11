//package com.project.openrun.wish.service.respository;
//
//import com.project.openrun.member.entity.Member;
//import com.project.openrun.member.entity.MemberRoleEnum;
//import com.project.openrun.member.repository.MemberRepository;
//import com.project.openrun.product.entity.Product;
//import com.project.openrun.product.repository.ProductRepository;
//import com.project.openrun.wish.repository.WishRepository;
//import com.project.openrun.wish.service.WishService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest
////@Transactional
////@Import(TestConfig.class)
//public class wishCountTest {
//
//    @Autowired
//    WishRepository wishRepository;
//
//    @Autowired
//    WishService wishService;
//
//    @Autowired
//    ProductRepository productRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    private List<Member> savedMember;
//
//    @BeforeEach
//    void setUp() {
//        // 1. 데이터 준비.
//        List<Member> members = new ArrayList<>();
//        for (int i = 0; i < 1000; i++) {
//
//            Member member = Member.builder()
//                    .memberName("test" + i)
//                    .memberEmail("test123" + i + "@naver.com")
//                    .memberPassword("12345")
//                    .memberRole(MemberRoleEnum.USER)
//                    .build();
//
//            members.add(member);
//        }
//
//        Product product = Product.builder()
//                .productName("product")
//                .wishCount(0) // 초기 찜 수는 0
//                .build();
//        savedMember = memberRepository.saveAll(members);
//        productRepository.save(product);
//    }
//
//    @AfterEach
//    void tearDown() {
//        wishRepository.deleteAll();
//        productRepository.deleteAll();
//        memberRepository.deleteAll();
//    }
//
//    @Test
//    @DisplayName("동시성 : 찜 개수")
//    void test() throws InterruptedException {
//
//        long point1 = System.currentTimeMillis();
//        int threadCount = 1000;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//
//        // 100개의 요청이 모두 끝날 때까지 기다리게 하기 위해서, CountDownLatch를 이용
//        CountDownLatch latch = new CountDownLatch(threadCount);
//        // CountDownLatch: 다른 스레드에서 수행중인 작업이 완료될때 까지 기다려주는 class
//
//        for (int i = 0; i < threadCount; i++) {
//            int ii = i;
//            executorService.submit(() -> {
//                try {
//                    wishService.createWish(1L, savedMember.get(ii));
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//        long point2 = System.currentTimeMillis();
//        // 확인하는 거니까 여기선 락을 안걸어도 됨
//        Product findProduct = productRepository.findById(1L).orElseThrow();
//        long point3 = System.currentTimeMillis();
//        // 100 -(1 * 100) = 0
//
//        System.out.println("[" + threadCount + "명이 동시 접속 ] point2 - point1 : " + (point2 - point1));
//        System.out.println("[상품 조회에 걸린 시간] point3 - point2 : " + (point3 - point2));
//        assertThat(findProduct.getWishCount()).isEqualTo(1000);
//    }
//}
