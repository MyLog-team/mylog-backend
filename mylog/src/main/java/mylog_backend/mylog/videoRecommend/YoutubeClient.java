package mylog_backend.mylog.videoRecommend;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
// Client : 무언가에 요청을 보내는 주체를 의미

// 여기서 Youtube API 서버에 요청을 보내는 HTTP Client 역할을 함
// 외부의 Youtube Data API와 통신을 담당하는 클래스
// 즉, 애플리케이션이 유튜브 API에 요청을 보낼때와 응답을 받고 파싱하는 일을 담당함
public class YoutubeClient {

    // YoutubeConfig 파일에서 api 키를 주입받음
    // 기존에 직접 env파일에서 주입 받았으나, SOC를 위해 역할을 분리
    private final String apiKey;

    // 스프링에서 제공하는 HTTP 통신용 클라이언트 객체
    // 외부 API 서버에 GET, POST 요청을 보내고 응답을 받게 해준다.
    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * keyword를 바탕으로 유튜브에 영상을 서치함
     * @param keyword : 유튜브에서 검색할 키워드
     * @return : 검색후 영상들중 5개를 추려낸 것
     */
    public List<VideoResponse> search(String keyword) {

        // UriComponentsBuilder : HTTP 요청 URL을 안전하게 만들때 사용하는 유틸리티 클래스
        // Youtube Data API v3의 엔드포인트에 요청을 보낼 URL 생성
        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search") // 일반 영상 검색에 요청
                // 쿼리 스트링 파라미터
                .queryParam("part", "snippet") // part : API 응답에 포함할 리소스 필드 / snippet : 응답에 제목, 설명, 썸네일 등 기본 정보를 포함시킴
                .queryParam("q", keyword) // 실제 검색 키워드
                .queryParam("type", "video") // 데이터를 필터링, 여기선 비디오만 응답에 포함
                .queryParam("maxResults", 5)  // 영상을 최대 5개까지 가져옴
                .queryParam("key", apiKey) // API 키를 가져옴
                .build()
                .toUriString(); // 앞선 쿼리 파라미터들을 최종 URL 문자열로 변환
        // https://www.googleapis.com/youtube/v3/search?part=snippet?q=keword ...

        // url을 String 타입으로 바꾸어 GET메서드를 호출 => youtube API에 GET 요청
        // 요청에 대한 결과를 response에 할당
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // getBody() : 응답 바디만 추출, 이때 응답 바디는 JSON 형태
        // 자바의 JSONObject로 변환(역직렬화 과정)
        JSONObject json = new JSONObject(response.getBody()); // 응답 본문 변환 String -> JSON Object

        // items : 유튜브 동영상 정보들을 할당
        // 그 배열들을 JSONArray 형태로 저장
        JSONArray items = json.getJSONArray("items");
        List<VideoResponse> videos = new ArrayList<>(); // items를 넣을 배열

        // 동영상 세부 정보 추출
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i); // i번째의 단일 item을 할당
            String videoId = item.getJSONObject("id").getString("videoId"); // item의 비디오 아이디
            JSONObject snippet = item.getJSONObject("snippet"); // item의 정보를 역직렬화
            String title = snippet.getString("title"); // 제목
            String thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url"); // 썸네일

            videos.add(new VideoResponse(title, videoId, thumbnailUrl));
        }

        return videos;
    }
}
