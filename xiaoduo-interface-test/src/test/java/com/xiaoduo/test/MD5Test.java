package com.xiaoduo.test;

public class MD5Test {

//    @Test
//    public void test() throws NoSuchAlgorithmException {
//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        String shop_id = "5d8328d4237402000e960ae8";
//        String task_id = "5d8c6d582374020001e389d3";
//        String order_id = "656701026656563558";
//        String buyer_nick = "sky韩紫乔";
//        String suffixStr = String.format("%s:%s:%s", buyer_nick, order_id, task_id);
//        byte[] digest = md5.digest(suffixStr.getBytes());
//        String result = HexUtils.toHexString(digest);
//        String format = String.format("tb:rmdsrv:state:taskstate:{tb:%s}:%s", shop_id, result);
//        System.out.println(format);
//    }
//
//    @Test
//    public void test1() throws NoSuchAlgorithmException {
//        String data = "{\"code\":0,\"message\":\"\",\"data\":{\"messages\":[1,2]}}";
//        JSONObject jsonObject = JSONObject.parseObject(data);
//        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("messages");
//        for (Object o : jsonArray) {
//            System.out.println(o);
//        }
//    }
//
//    @Test
//    public void test3() throws ApiException {
//        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/robot/send?access_token=aa9a76956e0bcf39e7d5ddccc5437fc9afd11e81fecb41569d4b5415dd95c8e9");
//        OapiRobotSendRequest request = new OapiRobotSendRequest();
//        request.setMsgtype("markdown");
//        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
//        markdown.setTitle("自动化测试");
//        markdown.setText("开始运行自动化测试：\n" +
//                "> 平台：拼多多\n" +
//                "> 功能：催单\n" +
//                "> ###### [点击查看](http://10.0.0.152:8080/job/java-interface-test/) \n");
//        request.setMarkdown(markdown);
//        OapiRobotSendResponse response = client.execute(request);
//    }
}
