java_binary (
    name = "BazelApp",
    srcs = glob(["src/main/java/cn/tuyucheng/taketoday/*.java"]),
    main_class = "cn.tuyucheng.taketoday.BazelApp",
    deps = ["//bazelgreeting:greeter", "@maven//:org_apache_commons_commons_lang3"]
)