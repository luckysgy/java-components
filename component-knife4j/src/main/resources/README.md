
```java
@Api(tags = "示例模块")
@RestController
public class DemoController {

    @ApiOperation(value = "保存数据")
    @PostMapping("/demo/save")
    public DemoUser save(@RequestBody DemoUser demoUser) {
        // ....
        return null;
    }
}
```