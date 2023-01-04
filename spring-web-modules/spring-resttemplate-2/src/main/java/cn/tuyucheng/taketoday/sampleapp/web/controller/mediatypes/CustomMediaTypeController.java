package cn.tuyucheng.taketoday.sampleapp.web.controller.mediatypes;

import cn.tuyucheng.taketoday.sampleapp.web.dto.TuyuchengItem;
import cn.tuyucheng.taketoday.sampleapp.web.dto.TuyuchengItemV2;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/", produces = "application/vnd.tuyucheng.api.v1+json")
public class CustomMediaTypeController {

    @RequestMapping(method = RequestMethod.GET, value = "/public/api/items/{id}", produces = "application/vnd.tuyucheng.api.v1+json")
    public @ResponseBody TuyuchengItem getItem(@PathVariable("id") String id) {
        return new TuyuchengItem("itemId1");
    }

    @RequestMapping(method = RequestMethod.GET, value = "/public/api/items/{id}", produces = "application/vnd.tuyucheng.api.v2+json")
    public @ResponseBody TuyuchengItemV2 getItemSecondAPIVersion(@PathVariable("id") String id) {
        return new TuyuchengItemV2("itemName");
    }
}