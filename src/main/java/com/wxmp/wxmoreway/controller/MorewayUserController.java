package com.wxmp.wxmoreway.controller;

import com.alibaba.fastjson.JSON;
import com.wxmp.core.exception.WxErrorException;
import com.wxmp.wxapi.process.MpAccount;
import com.wxmp.wxapi.process.WxApi;
import com.wxmp.wxapi.process.WxMemoryCacheClient;
import com.wxmp.wxmoreway.domain.MorewayUser;
import com.wxmp.wxmoreway.model.CurrencyResult;
import com.wxmp.wxmoreway.service.MorewayUserService;
import com.wxmp.wxmoreway.util.CookieUtils;
import com.wxmp.wxmoreway.util.HttpUrlConnection;
import com.wxmp.wxmoreway.util.RestUtil;
import com.wxmp.wxmoreway.util.XmlUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.alibaba.druid.util.Utils.md5;
import static com.github.wxpay.sdk.WXPayUtil.MD5;
import static com.wxmp.wxapi.process.WxApiClient.getJSTicket;
import static com.wxmp.wxapi.process.WxApiClient.getJSTicketCurr;

@Controller
@RequestMapping("moreway")
public class MorewayUserController {

    @Autowired
    private MorewayUserService morewayUserService;

    @Value("${param.appId}")
    private String appId;
    @Value("${param.mchId}")
    private String mchId;
    @Value("${param.key}")
    private String key;

    @GetMapping("")
    public String register(HttpServletRequest req, HttpServletResponse response){
        return "moreway/index";
    }

    @GetMapping("/kj")
    public String kj(HttpServletRequest req, HttpServletResponse response,Model model){
        String openId = CookieUtils.getCookieValue(req, "userOpenid");
        model.addAttribute("openId",openId);
        return "moreway/test";
    }

    @GetMapping("/loca")
    public String loca(HttpServletRequest req, HttpServletResponse response,Model model){
        String openId = CookieUtils.getCookieValue(req, "userOpenid");
        model.addAttribute("openId",openId);
        return "moreway/loca";
    }

    @GetMapping("/v1/sort")
    @ResponseBody
    public String sort(@RequestParam String ticket,
                       @RequestParam String timestamp,
                       @RequestParam String cardId,
                       @RequestParam String nonceStr,HttpServletRequest req){
        String openId = CookieUtils.getCookieValue(req, "userOpenid");
        ArrayList<String> list=new ArrayList<String>();
        list.add(ticket);
        list.add(timestamp);
        list.add(cardId);
        list.add(nonceStr);
        list.add(openId);
        Collections.sort(list);
        String result = DigestUtils.sha1Hex(list.get(0) + list.get(1) + list.get(2) + list.get(3) + list.get(4));
        return result;
    }

    @PostMapping("/wxpay")
    @ResponseBody
    public Map wxpay(@RequestParam float money,HttpServletRequest req, HttpServletResponse resp){
        System.out.println("我进来了,我要开始支付");
        String moneys = String.valueOf((int)(money * 100));
        System.out.println("获取到金额"+money);
        String xml = haveTestPayOrderXml(moneys,req);
        System.out.println("报文生成串:"+xml);
        String result = HttpUrlConnection.httpXML(WxApi.getUnifiedOrderUrl(), xml);
        Map map = XmlUtil.toMap(result);
        System.out.println("统一下单返回结果:"+map.toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Map mapp = new HashMap();
        mapp.put("appId",map.get("appid"));
        mapp.put("timeStamp",sdf.format(new Date()));
        mapp.put("nonceStr",randomUUID());
        mapp.put("signType","MD5");
        mapp.put("package","prepay_id="+map.get("prepay_id"));
        String sign = getSignToken(mapp);
        sign = (sign + "key=" + key);
        try {
            sign = MD5(sign).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapp.put("paySign",sign);
        System.out.println("返回支付接口数据信息:"+mapp.toString());
        return mapp;
    }

    //生成xml格式的订单
    public String haveTestPayOrderXml(String price,HttpServletRequest req){
        String openId = CookieUtils.getCookieValue(req, "userOpenid");
        System.out.println("从cookie中获取到的openId是"+openId);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
        String xml="";
        Map <String,String> map=new HashMap<String,String>();
        map.put("appid", appId);
        map.put("mch_id", mchId);
        //map.put("device_info", params.get("avm"));
        map.put("nonce_str", randomUUID());
        map.put("detail", "200qb");
        String out_trade_no="99"+sdf.format(new Date());
        map.put("out_trade_no", out_trade_no);
        System.out.println(out_trade_no);
        map.put("total_fee",price);//把页面传过来的价格price放到订单map里，其他参数同理可传进来
        map.put("openid", openId);//这里的openid是个写死的例子，大家可以根据自己获取到的openid放在这里
        map.put("spbill_create_ip", getLocalIp(req));
        map.put("notify_url", "https://www.cnovotech.com/moreway/notify");//这里是微信支付结果通知的接口，用来接收订单支付情况
        map.put("trade_type", "JSAPI");
        map.put("product_id", "12235413214070356123321");
        String body = "aa";
        try {
            body  = new String(body.toString().getBytes("ISO8859-1"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("body", body);
        String sign = getSignToken(map);
        sign = (sign + "key=" + key);
        try {
            sign = MD5(sign).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(sign);
        map.put("sign", sign);
        xml= XmlUtil.toXml(map);
        System.out.println("终端ip是:"+getLocalIp(req));
        return xml;
    }

    public static String getSignToken(Map<String, String> map) {
        String result = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {

                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }
            }
            result = sb.toString();
            //进行MD5加密
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public static String randomUUID(){
        String uuid = UUID.randomUUID().toString();
        return StringUtils.remove(uuid, "-");
    }

    /**
     * 从Request对象中获得客户端IP，处理了HTTP代理服务器和Nginx的反向代理截取了ip
     * @param request
     * @return ip
     */
    public static String getLocalIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwarded = request.getHeader("X-Forwarded-For");
        String realIp = request.getHeader("X-Real-IP");

        String ip = null;
        if (realIp == null) {
            if (forwarded == null) {
                ip = remoteAddr;
            } else {
                ip = remoteAddr + "/" + forwarded.split(",")[0];
            }
        } else {
            if (realIp.equals(forwarded)) {
                ip = realIp;
            } else {
                if(forwarded != null){
                    forwarded = forwarded.split(",")[0];
                }
                ip = realIp + "/" + forwarded;
            }
        }
        return ip;
    }

    /**
     * 支付成功后的回调函数
     */
    @ResponseBody
    @RequestMapping("/notify")
    public String notify(HttpServletRequest req, HttpServletResponse resp){
        System.out.println("支付通知获取!!!");
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());//设置日期格式
        return now;
    }

    @GetMapping("/v1")
    public String getOAuthCodeUrl(String shareOpenId, HttpServletResponse response) throws IOException {
        MpAccount mpAccount = WxMemoryCacheClient.getMpAccount();
        String appId = mpAccount.getAppid();
        String redirectUrl = "https://www.cnovotech.com/moreway/v1/getUserInfo?shareOpenId="+shareOpenId;
        String scope = "snsapi_userinfo";//snsapi_base;
        String state = "state";
        String url = WxApi.getOAuthCodeUrl(appId,redirectUrl,scope,state);
        return "redirect:" + url;
    }

    @GetMapping("/v1/getUserInfo")
    public String getUserInfo(String shareOpenId, String code, String state, Model model){
        MpAccount mpAccount = WxMemoryCacheClient.getMpAccount();
        String appId = mpAccount.getAppid();
        System.out.println("appId是:"+appId);
        String appSecret = mpAccount.getAppsecret();
        System.out.println("appSecret是:"+appSecret);
        System.out.println("进来了___________________code是:______________________" + code);
        String result = morewayUserService.getUserInfo(code, appId, appSecret);
        System.out.println("获取到用户信息是:______________________" + result);
        Map map = JSON.parseObject(result);
        model.addAttribute("result",map);
        model.addAttribute("shareOpenId",shareOpenId);
        MorewayUser mu = new MorewayUser();
        mu.setOpenId((String)map.get("openid"));
        List<MorewayUser> morewayUsers = morewayUserService.getMorewayUserInfo(mu);
        if(morewayUsers.isEmpty()){
            return "moreway/register";
        }
        return "moreway/success";
    }

    @GetMapping("/v1/ticket")
    @ResponseBody
    public Map ticket(@RequestParam String type){
        MpAccount mpAccount = WxMemoryCacheClient.getMpAccount();//获取缓存中的唯一账号
        String appId = mpAccount.getAppid();
        String jsTicket = "";
        try {
            jsTicket = getJSTicketCurr(mpAccount, type);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }
        Map map = new HashMap();
        map.put("appId",appId);
        map.put("jsTicket",jsTicket);
        System.out.println("卡劵获取的签名码是:"+map.toString());
        return map;
    }


    @PostMapping("/v1/userAdd")
    @ResponseBody
    public CurrencyResult userAdd(@RequestBody MorewayUser morewayUser,HttpServletRequest req){
//        String openId = CookieUtils.getCookieValue(req, "userOpenid");
//        System.out.println(openId);
//        morewayUser.setOpenId(openId);
        /*String randomStr = CookieUtils.getCookieValue(req, "randomStr");
        System.out.println(morewayUser.getRandomStr());
        System.out.println("cookie:"+randomStr);
        morewayUser.setCreateTime(new Date());
        if(morewayUser.getRandomStr() != null && randomStr != null){
            if(randomStr.equals(morewayUser.getRandomStr())){
                return morewayUserService.userAdd(morewayUser);
            }
        }
        return new CurrencyResult(301, "手机验证码输入错误", 0);*/
        morewayUser.setCreateTime(new Date());
        return morewayUserService.userAdd(morewayUser);
    }

    @GetMapping("paySuccess")
    public String paySuccess(HttpServletRequest request, HttpServletResponse response){
        return "moreway/paySuccess";
    }

    @GetMapping("registerSuccess")
    public String registerSuccess(HttpServletRequest request, HttpServletResponse response,Model model){
        String openId = CookieUtils.getCookieValue(request, "userOpenid");
        model.addAttribute("openId",openId);
        return "moreway/registerSuccess";
    }


    @GetMapping("/v1/sendMsg")
    @ResponseBody
    public CurrencyResult sendMsg(@RequestParam String phone,HttpServletResponse response){
        String randomStr = getRandomStr(6, 0);
        String content = "您的验证码是："+randomStr+"。请不要把验证码泄露给其他人。";
        Cookie cookie = new Cookie("randomStr", randomStr);
        cookie.setMaxAge(60);
        response.addCookie(cookie);
        System.out.println(content);
        String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";
        String Urls = Url + "&account=C99368961&password=63d71f9f5168f0a96d27536c3e01d8a4&mobile="+phone
                +"&content="+content+"&format=json";
        String result = RestUtil.httpGet(Urls);
        Map map = JSON.parseObject(result);
        if((Integer)map.get("code") == 2){
            return new CurrencyResult(200,"查询成功",1);
        }
        return new CurrencyResult(300,"查询失败",0);
    }
    /**
     * 随机产生字符串
     *
     * @param length 字符串长度
     *
     * @param type 类型 (0: 仅数字; 2:仅字符; 别的数字:数字和字符)
     * @return
     */
    public static String getRandomStr(int length, int type)
    {
        String str = "";
        int beginChar = 'a';
        int endChar = 'z';
        // 只有数字
        if (type == 0)
        {
            beginChar = 'z' + 1;
            endChar = 'z' + 10;
        }
        // 只有小写字母
        else if (type == 2)
        {
            beginChar = 'a';
            endChar = 'z';
        }
        // 有数字和字母
        else
        {
            beginChar = 'a';
            endChar = 'z' + 10;
        }
        // 生成随机类

        Random random = new Random();
        for (int i = 0; i < length; i++)
        {
            int tmp = (beginChar + random.nextInt(endChar - beginChar));
            // 大于'z'的是数字
            if (tmp > 'z')
            {
                tmp = '0' + (tmp - 'z');
            }
            str += (char) tmp;
        }

        return str;
    }
}
