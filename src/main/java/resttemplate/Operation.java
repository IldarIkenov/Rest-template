package resttemplate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import resttemplate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class Operation {

    private HttpHeaders headers;
    private RestTemplate template;
    private String URL = "http://91.241.64.178:7081/api/users";

    public Operation(HttpHeaders headers, RestTemplate template) {
        this.headers = headers;
        this.headers.set("Cookie",
                String.join(";", template.headForHeaders(URL).get("Set-Cookie")));
        this.template = template;
    }

    public String getAnswer() {
        return addUser().getBody() +
                updateUser().getBody() +
                deleteUser().getBody();
    }

    private List<User> getAllUsers() {
        ResponseEntity<List<User>> responseEntity = template.exchange(
                URL, HttpMethod.GET, null, new ParameterizedTypeReference<List<User>>() {});
        System.out.println(responseEntity.getHeaders());
        return responseEntity.getBody();
    }

    // POST .../api/users
    private ResponseEntity<String> addUser() {
        User user = new User(3L, "James", "Brown", (byte) 5);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        return template.postForEntity(URL, entity, String.class);
    }

    // PUT .../api/users
    private ResponseEntity<String> updateUser() {
        User user = new User(3L, "Thomas", "Shelby", (byte) 5);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        return template.exchange(URL, HttpMethod.PUT, entity, String.class, 3);
    }

    // DELETE .../api/users
    private ResponseEntity<String> deleteUser() {
        Map<String, Long> uriVariables = new HashMap<>() {{
            put("id", 3L);
        }};
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return template.exchange(URL + "/{id}", HttpMethod.DELETE, entity, String.class, uriVariables);
    }
}
