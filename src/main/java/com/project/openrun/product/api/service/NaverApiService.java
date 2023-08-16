package com.project.openrun.product.api.service;


import com.project.openrun.product.api.dto.CreateDataRequestDto;
import com.project.openrun.product.api.dto.NaverDto;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NaverApiService {

    private final RestTemplate restTemplate;
    private final NaverUriBuilderService naverUriBuilderService;
    private final ProductRepository productRepository;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    // 상품 카테고리
    private static final HashMap<String, ArrayList<String>> PRODUCT_ITEMS;
    private static final String[] ITER_STRING;


    static {
        PRODUCT_ITEMS = new HashMap<>();
        // 상의, 하의, 신발, 가방, 모자, 노트북, 스마트폰, 가전, 향수, ...
        PRODUCT_ITEMS.put("아우터", new ArrayList<>(Arrays.asList("빌리버스","UNKNOWN","스콰즈","옷자락","몽클레르"/*,"엘케이트","바보사랑","현대모비스","스톤아일랜드","노브랜드","막스마라","아디다스","노스페이스","버버리","에르노","보드미","나이키","올리비아로렌","휠라","바버","톰브라운","울리치","파라점퍼스","오노마","돌체앤가바나","1300K","CP컴퍼니","디스퀘어드2","원픽","파파브로","브루넬로쿠치넬리",
                "프라다","캐나다구스","발망","무스너클","페이","리더스","오프화이트","발렌티노","팜엔젤스","릭오웬스","지프","알렉산더맥퀸","발렌시아가","구찌","빈폴","딸리아또레","아페쎄","텐바이텐","리스트","메종마르지엘라","베르사체","내셔널지오그래픽","아미","쉬즈미스","겐조","뉴발란스","랜더스","하늘","에잇세컨즈","이자벨마랑","타미힐피거","보테가베네타","질샌더","생로랑","푸마","프렐린","GANNI","티아이포맨",
                "언더아머","에트로","골든구스","아스페시","지방시","폴로랄프로렌","웨일테일","시마노","올포유","스텔라매카트니","핀코","BCBG","모스키노","자라","COZY","디엠에스","플라스틱아일랜드","MSGM","듀베티카","윌슨","라코스테","아날도바시니","PAT","ab.f.z","퍼피아","꼼데가르송","크로커다일레이디","지센","프로월드컵","자크뮈스","수키"*/))); // 8
        PRODUCT_ITEMS.put("상의", new ArrayList<>(Arrays.asList("UNKNOWN","나이키","엠씨엔","아디다스","자라"/*,"타미힐피거","폴로랄프로렌","리스트","라코스테","ab.f.z","쉬즈미스","랩","게스","올리비아로렌","BNX","미쏘","헤지스","잇미샤","라인","시스티나","폴로","포커스","JAJU","레코브","지컷","COS","비지트인뉴욕","에고이스트","보니스팍스","반에이크","보브","에잇세컨즈","러브앤쇼","베스띠벨리",
                "나인","ZOOC","르샵","꼼빠니아","CLOVIS","폴햄","베네통","부루앤쥬디","더아이잗","트위","PAT","SI","지고트","enc","로엠","아뜨랑스","더웨이나인","BCBG","탑텐","엠씨","온앤온","제시뉴욕","제너럴아이디어","지오다노","크로커다일레이디","GGPX","리안뉴욕","SOUP","나이스클랍","블루페페","JJ지고트","지센","레노마레이디스","모조에스핀","샤틴","아이잗바바","씨씨콜렉트","샤트렌","시슬리","플라스틱아일랜드",
                "난닝구","발렌시아","헤지스레이디스","라인어디션","코인코즈","올리비아하슬러","스튜디오톰보이","레니본","피그먼트","써스데이아일랜드","오조크","더뽐","크로커다일","케네스레이디","마리끌레르","올리브데올리브","플리츠미","안지크","살롱드쥬","미엘","베이지크","라우렐","듀엘","빈폴레이디스","무자크","에이비플러스"*/)));
        PRODUCT_ITEMS.put("하의", new ArrayList<>(Arrays.asList("나이키","아디다스","비비안","리바이스","안다르"/*,"폴로랄프로렌","지프","타미힐피거","레노마","바쏘옴므","라코스테","스톤아일랜드","빠니깔레","파타고니아","버커루","뱅뱅","챔피온","라이프워크","BON","리버클래시","디키즈","내셔널지오그래픽","지오송지오","클라이드앤","뉴에라","게스","인디안","CP컴퍼니","다이나핏","NFL","지오지아","JAJU","닉스","바쏘",
                "폴햄","킨록","장롱","티아이포맨","HotCode","트레몰로","헤지스","그라미치","에잇세컨즈","NBA","오어슬로우","앤드지","지오다노","올젠","엄브로","칼하트WIP","PAT","웰파","에디션","STCO","브렌우드","칼하트","행텐","바나나리퍼블릭","마인드브릿지","유니클로","용된다","폴로","체이스컬트","트루젠","빈폴","MLB","MIR","지이크","탑텐","프로젝트M","심플리","RZ지오지아","파파브로","후아유","커스텀멜로우",
                "에디션앤드지","커버낫","프랭키뉴욕","샙","시리즈","캉골","스파오","아킵","TNGT","프랑코페라로","헨리코튼","컨셉원","로가디스","흄","뭉크","카이아크만","산토초이","앤듀","펠틱스","TBJ","릴리전","다니엘크레뮤","스메르","화이트워터보이즈","갤럭시라이프스타일"*/)));
        PRODUCT_ITEMS.put("모자", new ArrayList<>(Arrays.asList("UNKNOWN","뉴에라","버버리","네파","내셔널지오그래픽"/*,"프로스펙스","에잇세컨즈","타미진스","LEE","코닥어패럴","LIMS","노스페이스","47BRAND","칼하트","타미힐피거","슈펜","코오롱스포츠","동대문모자","피에르가르뎅","헤네스","이미스","나이키","NBA","휠라","화이트샌즈","아메리칸니들","커버낫","밀로","베루툼","구김스","베네노","슈프림",
                "스톤아일랜드","오클리","파타고니아","DIOR","록시","ENVY","데우스엑스마키나","메르헨","굿즈인","닥스","자크뮈스","엄브로","K2","캘빈클라인","스노우피크","JAJU","플리퍼","썬글레이드","MLB","폴로랄프로렌","GANNI","제이제이나인","비비안웨스트우드","후아유","로스코","커먼하우스","하바행크","캠모아","스콰즈","캉골","레노마","바잘","아크테릭스","빌라봉","앙상블","배럴","3M","에콴디노","지프",
                "캡텐","햇츠온","아가타","UNDERCONTROL","폴로","빈폴ACC","벤시몽","타입서비스","헬스투오","아디다스","헬렌카민스키","구찌","라코스테","데상트","Calvin Klein Jeans","라이프워크","이코마켓","우알롱","SONHADOR","언더아머","뉴발란스","스투시","디스커버리익스페디션","퀵실버","노스페이스화이트라벨","JORDAN","아이엠써니","뷰랩","위크나인"*/)));
        /*PRODUCT_ITEMS.put("신발", new ArrayList<>(Arrays.asList("UNKNOWN", "탠디","나이키","아디다스","에스콰이아","뉴발란스","미소페","소다","크록스","엘칸토","푸마","반스","컨버스","스케쳐스","아식스","알렉산더맥퀸","발렌티노","락포트","휠라","토즈","닥스","허시파피","골든구스","발렌시아가","고세","구찌","언더아머","세라","핏플랍","프라다","베어파우","메종마르지엘라","프로스펙스","리복","AUTRY",
                "캠퍼","JORDAN","제옥스","미즈노","버버리","BABARA","라코스테","타미힐피거","발리","콜한","노스페이스","살로몬","K2","슈펜","호카오네오네","르까프","써코니","탐스","오니츠카타이거","월드컵","샤넬","커먼프로젝트","수페르가","꼼데가르송","아키클래식","레페토","셀린느","코치","미하라야스히로","데상트","네파","로에베","디스커버리익스페디션","MLB","프레드페리","케즈","슈콤마보니","호카","벤시몽",
                "로로피아나","아이더","다이나핏","록시","페이퍼플레인","행텐","폴로","슬레진저","블루마운틴","밸롭","빅토리아","빅토리아슈즈","락피쉬웨더웨어","아레나","엘르","르무통","배럴","유세븐","런웨이브","아쿠런","워터런","스위스런","위크나인","디지지","808","이츠라이프")));
        PRODUCT_ITEMS.put("가방", new ArrayList<>(Arrays.asList("UNKNOWN","보테가베네타","구찌","키플링","프라다","버버리","코치","발렌티노","루이 비통","마이클코어스","생로랑","아디다스","발렌시아가","끌로에","마르니","아페쎄","토리버치","펜디","에트로","살바토레페라가모","나이키","마크제이콥스","노스페이스","메종마르지엘라","셀린느","에스콰이아","멀버리","미우미우","쌤소나이트","샤넬","닥스","로에베",
                "발리","DIOR","헤지스","피에르가르뎅","휠라","비비안웨스트우드","라코스테","엘레강스","롱샴","산리오","캉골","메트로시티","레스포색","만다리나덕","쿠론","내셔널지오그래픽","자라","질스튜어트","토즈","루즈앤라운지","루이까또즈","에르메스","칼하트","디스커버리익스페디션","아크테릭스","입생로랑","고야드","질스튜어트뉴욕","슈펜","드래곤디퓨전","사만사타바사","MLB","조셉앤스테이시","COS","조이그라이슨","팩세이프",
                "Calvin Klein Jeans","쌤소나이트레드","노스페이스화이트라벨","랩","빈폴ACC","스노우피크","오주코","캐스키드슨","오야니","마뗑킴","스타벅스","바오바오","제이에스티나","몽삭","세인트스코트","바쿠","찰스앤키스","파인드카푸어","분크","플리츠마마","아이띵소","로사케이","맥끌라니","마르헨제이","세이모온도","칼린","크로스","루에브르","브루노말리","리솜","마테마틱","메띠에하르")));
        PRODUCT_ITEMS.put("시계", new ArrayList<>(Arrays.asList("unknown","바몬트", "쟈켓 드로", "코르움", "듀보아", "포르셰 디자인", "파라미길", "몽블랑", "쥴리 우스 누리", "F.P. 죠른", "하리스 톤", "쟈켓 드로", "듀페트라", "신계", "에르미스", "차움미", "불가리", "악셉터", "알펜시아", "디젤", "폴스미스", "담합", "레이몬드 웨일", "폴리티", "모비도", "노모스", "헬백스", "제라르 페르고", "코닝키",
                "지니브", "벨 & 로스", "매뉴팔 운터", "엘리벳", "멀버리", "그라이프르", "케네스 콜", "팔레디움", "아르마니", "마빈", "페라리", "로터리", "스와로브스키", "조르지오 아르마니", "마이클 코어스", "보슈롱", "애니 클라인", "스톰", "스킨스워치", "라슨 & 제닝스", "비발디", "톰미 힐피거","파텍 필립", "브레게", "오데마 피게", "바세론 콘스탄틴", "운트 죄네", "글라슈테", "피아제", "르쿨트르", "블랑팡", "리차드 밀",
                "로저 드뷔", "롤렉스", "브라이 틀링", "파네라이", "IWC", "오메가", "위블로", "라도", "태그 호이어", "론진", "튜더", "그랜드 세이코", "까르띠에", "프레드릭 콘스탄트", "미도", "오리스", "G-Shock", "시티즌", "티소", "세이코", "해밀턴", "카시오")));
        PRODUCT_ITEMS.put("노트북", new ArrayList<>(Arrays.asList("unknown","애플", "델", "HP", "레노버", "아수스", "에이서", "마이크로소프트", "소니", "토시바", "삼성", "LG", "후지쯔", "파나소닉", "MSI", "레이저", "화웨이", "게이트웨이", "에일리언웨어", "컴팩", "바이오", "구글", "비지오", "클레보", "시스템76", "샤오미", "기가바이트", "세이저", "오리진 PC", "AORUS", "메디온", "샤프", "메인기어",
                "사이버파워PC", "조탁", "벤큐", "NEC", "아이볼", "아비타", "하세", "벨로시티 마이크로", "엘루크트로닉스", "포르쉐 디자인", "추위", "RCA", "테클라스트", "점퍼", "슈나이더", "마이크로맥스", "프레스티지오", "퓨전5")));
        PRODUCT_ITEMS.put("가전제품", new ArrayList<>(Arrays.asList("기타주방가전", "정수기", "전용냉장고", "냉장고", "전기포트", "식기세척/건조기", "식기세척기", "건조기", "믹서기", "분쇄기", "커피머신", "전기쿠커", "커피메이커", "전기밥솥", "전기그릴", "거품기", "반죽기", "진공포장기", "식품건조기", "와플제조기", "오븐", "인덕션", "두부두유제조기", "에어프라이어", "아이스크림제조기", "녹즙기", "빙수기", "토스터기",
                "가스레인지", "김치냉장고", "핸드블렌더", "제빵기", "업소용빙수기", "요구르트제조기", "냉동고", "가스레인지후드", "업소용믹서기", "전자레인지", "튀김기", "업소용진공포장기", "업소용튀김기", "업소용거품/반죽기", "핫플레이트", "홍삼제조기", "음식물처리기", "샌드위치제조기", "전기팬", "하이라이트", "탄산수제조기", "커피자판기", "생선그릴", "죽제조기", "기타주방가전부속품", "업소용음식물처리기", "하이브리드", "청소기", "해충퇴치기",
                "구강청정기", "건조기/탈수기", "세탁기", "무전기", "재봉틀", "스탠드", "다리미", "전화기", "디지털도어록", "보풀제거기", "자외선소독기", "핸드드라이어", "손소독기", "의류관리기", "업소용자외선소독기", "연수기", "전신건조기", "이온수기", "이어폰/헤드폰액세서리", "마이크", "스피커", "블루투스셋", "리시버/앰프", "이어폰", "오디오", "헤드폰", "턴테이블", "라디오", "홈시어터", "오디오믹서", "방송음향기기", "MP3", "MP3/PMP액세서리",
                "CD플레이어", "카세트플레이어", "DAC", "노래반주기", "데크", "튜너", "PMP", "MD플레이어", "휴대용게임기", "PC게임")));
        PRODUCT_ITEMS.put("건강식품", new ArrayList<>(Arrays.asList("나우푸드", "라이프익스텐션", "솔가", "닥터스베스트", "재로우포뮬러스", "내츄럴플러스", "종근당건강", "캘리포니아골드뉴트리션", "GNC", "종근당", "하이웰", "네추럴라이즈", "스포츠리서치", "일양약품", "뉴트리원", "GNM자연의품격", "세노비스", "뉴트리디데이", "락토핏", "블랙모어스", "닥터린", "한미양행", "JW중외제약", "에스더포뮬러", "덴프스", "뉴트리코어", "프롬바이오",
                "유한양행", "안국건강", "레이델", "고려은단", "광동", "커클랜드", "데일리원", "바이오가이아", "순수식품", "비에날씬", "콤비타", "히말라야", "웰빙곳간", "더리얼", "암웨이", "셀렉스", "드시모네", "녹십자웰빙", "뉴트리라이트", "함소아", "일동제약", "락피도", "GC녹십자", "애터미", "차일드라이프", "인테로", "젠와이즈", "주영엔에스", "대웅제약", "락티브", "유유제약", "그린스토어", "데이즈온", "듀오락", "닥터아돌", "피토틱스", "프로메가",
                "유니시티", "바른", "한미", "전립소", "트루포뮬러", "여에스더", "뉴스킨", "트루락", "더작", "콴첼", "포뉴", "유니베라", "자노탁트", "아사히", "아이클리어", "뉴네이처", "쁘띠앤", "바이오렉트라", "트루엔", "대웅", "웰릿", "크릴56", "뉴트리모어", "비에날17", "닥터파이토", "와이즈바이옴", "쏘팔코사놀", "스파톤", "엘레나", "키즈텐", "ALLBARUN", "킹프리미엄프로바이오틱스", "스피루리나", "이영애의건강미식", "오늘의밸런스", "PH365")));*/


        ITER_STRING = new String[]{"[신상]", "[인기]", "[중고]", "[해외배송]", "", "[무료배송]", "[빠른배송]", "[핫딜]", "[정품]", "[최시우 상품]"};
    }


    public void createItemForNaverApi(CreateDataRequestDto requestDto) {
        // 첫 for 문 -> display:100, start:1  /  display:100, start:101 / display:100, start:201  /  display:100, start:301 / ..  /  display:100, start:901 / display:100 , start:1000
        int display = 100;

        for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : PRODUCT_ITEMS.entrySet()) {

            String category = stringArrayListEntry.getKey();

            stringArrayListEntry.getValue().parallelStream().forEach(value -> {
                int i = 1;
                while (true) {

                    if (i > 1000) {
                        break;
                    }

                    URI uri = naverUriBuilderService.buildUriByQueryAndDisplayAndStart(value + category, display, i);

                    i += 100;

                    // 보내줄 헤더 정보. 메타 정보
                    RequestEntity<Void> voidRequestEntity = RequestEntity
                            .get(uri)
                            .header("X-Naver-Client-Id", clientId)
                            .header("X-Naver-Client-Secret", clientSecret)
                            .build();

                    // 반환 결과
                    NaverDto naverDto;
                    try {
                        naverDto = restTemplate.exchange(voidRequestEntity, NaverDto.class).getBody();
                    } catch (RestClientException e) {
//                        throw new ResponseStatusException(WRONG_INPUT.getStatus(), WRONG_INPUT.getMessageTemplate());
                        break;
                    }

                    if (ObjectUtils.isEmpty(naverDto.naverItemResponseDtoList())) {
                        log.error("[NaverApiService createItemForNaverApi] no itemResponseDtoList");
//                        new ResponseStatusException(NO_SEARCH_DATA.getStatus(),NO_SEARCH_DATA.getMessageTemplate());
                        break;
                    }

                    List<Product> products = new ArrayList<>();

                    naverDto.naverItemResponseDtoList().forEach((dto) -> {
                        Arrays.stream(ITER_STRING).forEach((string) -> {
                            Product newProduct = Product.builder()
                                    .price(Integer.valueOf(dto.price()))
                                    .productImage(dto.image())
                                    .productName(string + dto.productName())
                                    .category(category)
                                    .mallName(dto.mallName())
                                    .currentQuantity(30)    // 여기는 메서드로 랜덤하게 넣어주는 방향도 고려
                                    .eventStartTime(setDate())
                                    .totalQuantity(30)
                                    .wishCount(0)
                                    .status(OpenRunStatus.WAITING)
                                    .build();
                            products.add(newProduct);
                        });
                    });

                    // bulk 연산 적용 테스트 확인 필요
                    productRepository.saveAll(products);
                    System.out.println("products.size() = " + products.size());
                }
            });
        }
    }

    private LocalDateTime setDate() {
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.of(2023, Month.AUGUST, 31);

        LocalDate randomDate = getRandomDateBetween(from, to);

        // 랜덤 날짜에 9시 설정
        LocalDateTime dateTime = randomDate.atTime(9, 0, 0);

//        System.out.println(dateTime);

        return dateTime;
    }

    private static LocalDate getRandomDateBetween(LocalDate from, LocalDate to) {
        long totalDays = ChronoUnit.DAYS.between(from, to);
        long randomDays = ThreadLocalRandom.current().nextLong(totalDays);

        return from.plusDays(randomDays);
    }

}
