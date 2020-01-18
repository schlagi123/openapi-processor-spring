/*
 * This class is auto generated by https://github.com/hauner/openapi-generatr-spring.
 * DO NOT EDIT.
 */

package generated.api;

import generated.model.Props;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface Api {

    @GetMapping(path = "/endpoint-object")
    ResponseEntity<Void> getEndpointObject(Props props);

    @GetMapping(path = "/endpoint-map")
    ResponseEntity<Void> getEndpointMap(@RequestParam Map<String, String> props);

    @GetMapping(path = "/endpoint-multi-map")
    ResponseEntity<Void> getEndpointMultiMap(@RequestParam MultiValueMap<String, String> props);

}
