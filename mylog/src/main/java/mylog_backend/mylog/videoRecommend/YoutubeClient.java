package mylog_backend.mylog.videoRecommend;

import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
// Client : 무언가에 요청을 보내는 주체를 의미

// 여기서 Youtube API 서버에 요청을 보내는 HTTP Client 역할을 함
// 외부의 Youtube Data API와 통신을 담당하는 클래스
// 즉, 애플리케이션이 유튜브 API에 요청을 보낼때와 응답을 받고 파싱하는 일을 담당함
public class YoutubeClient {

    private final YoutubeProperties youtubeProperties;

    // 스프링에서 제공하는 HTTP 통신용 클라이언트 객체
    // 외부 API 서버에 GET, POST 요청을 보내고 응답을 받게 해준다.
    private final RestTemplate restTemplate = new RestTemplate();


    /**
     * keyword를 바탕으로 유튜브에 영상을 서치함
     * @param keyword : 유튜브에서 검색할 키워드
     * @return : 검색후 영상들중 5개를 추려낸 것
     */
    public List<YoutubeVideo> search(String keyword) {

        // 요청을 보낼 기본 URL, 여기서 유튜브 API의 search 엔드포인트를 사용함
        // UriComponentsBuilder : HTTP 요청 URL을 안전하게 만들때 사용하는 유틸리티 클래스
        String url = UriComponentsBuilder.fromHttpUrl("https://www.googleapis.com/youtube/v3/search")
                // 쿼리 스트링 파라미터
                .queryParam("part", "snippet") // part : API 응답에 포함할 리소스 필드 / snippet : 응답에 제목, 설명, 썸네일 등 기본 정보를 포함시킴
                .queryParam("q", keyword) // 실제 검색 키워드
                .queryParam("type", "video") // 데이터를 필터링, 여기선 비디오만 응답에 포함
                .queryParam("maxResults", 5)  // 영상을 최대 5개까지 가져옴
                .queryParam("key", youtubeProperties.getApiKey()) // API 키를 가져옴
                .build()
                .toUriString(); // 앞선 쿼리 파라미터들을 최종 URL 문자열로 변환

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        JSONObject json = new JSONObject(response.getBody()); // 응답 본문 변환 String -> JSON Object

        // 유튜브 API 응답 JSON에는 여러 동영상 정보다 items 배열에 들어감
        // 그 배열들을 JSONArray 형태로 저장
        JSONArray items = json.getJSONArray("items");
        List<YoutubeVideo> videos = new ArrayList<>();

        // 동영상 세부 정보 추출
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i); // 단일 아이템
            String videoId = item.getJSONObject("id").getString("videoId"); // 비디오 아이디
            JSONObject snippet = item.getJSONObject("snippet");
            String title = snippet.getString("title"); // 제목
            String thumbnailUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url"); // 썸네일

            videos.add(new YoutubeVideo(videoId, title, thumbnailUrl));
        }

        return videos;
    }
}
