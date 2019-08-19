/**
 * 公共方法
 * @author zhaoqf
 */
(function ($, util) {
    /**
     * 异步加载js文件,
     * 已存在不会重新加载,顺序加载
     *
     * @param src           string或array
     * @param callback      回调函数
     */
    util.loadJs = function (src, callback) {
        if (typeof src == "string") {
            src = [src];
        }

        var jsarr = [], scriptArr = document.getElementsByName("script");
        for (var i = 0; i < scriptArr.length; i++) {
            jsarr.push(scriptArr[i].src);
        }
        function load() {
            if (src.length > 0) {
                var link = src.shift();
                if (util.indexOfArray(jsarr, link) > -1) {
                    return load();
                }
                var head = document.getElementsByTagName('head')[0];
                var script = document.createElement('script');
                script.type = 'text/javascript';
                script.src = link;
                script.onload = load;
                head.appendChild(script);
            } else {
                typeof callback == "function" && callback();
            }
        }

        load();
    };

    /**
     * 执行js文件,不产生script标签
     *
     * @param src           string或array
     * @param callback      回调函数
     */
    util.executeJs = function (src, callback) {
        if (typeof src == "string") {
            src = [src];
        }
        if (src.length > 0) {
            var url = src.shift();
            $.ajax({
                type: 'get',
                url: url,
                dataType: 'script',
                success: function () {
                    if (src.length = 0) {
                        callback && callback();
                    } else {
                        executeJs(src, callback);
                    }
                }
            });
        }
    };

    /**
     * 异步加载css文件,
     * 已存在不会重新加载,顺序加载
     * @param src           string或array
     * @param callback      回调函数
     */
    util.loadCss = function (href, callback) {
        if (typeof href == "string") {
            href = [href];
        }
        var cssarr = [], linkArr = document.getElementsByName("link");
        for (var i = 0; i < linkArr.length; i++) {
            cssarr.push(linkArr[i].href);
        }
        function load() {
            if (href.length > 0) {
                var url = href.shift();
                if (util.indexOfArray(cssarr, url) > -1) {
                    return load();
                }
                var head = document.getElementsByTagName('head')[0];
                var link = document.createElement('link');
                link.rel = "stylesheet";
                link.type = "text/css";
                link.href = url;
                link.onload = load;
                head.appendChild(link);
            } else {
                typeof callback == "function" && callback();
            }
        }

        load();
    };

    /**
     * 获取url中的参数
     *
     * @param name
     * @returns {*}
     */

    util.getUrlParam = function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null; //返回参数值
    };

    /**
     * 判断是否有定义
     *
     */
    util.isDefined = function (obj) {
        return typeof(obj) != "undefined";
    };
    /**
     * 判断对象是否是个方法
     *
     * @param obj
     * @returns {boolean}

     */

    util.isFunction = function isFunction(obj) {
        if (obj && typeof(obj) == "function") {
            return true;
        } else {
            return false;
        }
    };

    /**
     * 获取值在数组中下标
     *
     *
     * @param arr
     * @param val
     */
    util.indexOfArray = function (arr, v) {
        if (!Array.isArray(arr)) {
            return -1;
        }
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] == v) {
                return i
            }
        }
        return -1;
    };

    /**
     * 删除数组中指定值
     *
     * @param arr
     * @param val
     */
    util.removeFromArray = function (arr, v) {
        var index = util.indexOfArray(arr, v);
        if (index > -1) {
            arr.splice(index, 1);
        }
        return arr;
    };

    /**
     * push非重复值
     *
     * @param arr
     * @param val
     */
    util.pushArray2 = function (arr, v) {
        if (util.indexOfArray(arr, v) == -1) {
            arr.push(v);
        }
        return arr;
    };

    /**
     * 获取id下所有表单元素的键值对
     *
     * @param formId

     */
    util.getFormData = function (formId) {
        var field = [];
        var fieldElem = $('#' + formId).find('input,select,textarea');
        $.each(fieldElem, function (_, item) {
            if (!item.name) return;
            if (/^checkbox|radio$/.test(item.type) && !item.checked) return;
            field[item.name] = $(item).val();
        });
        return field;
    }

    function encodeUTF8(s) {
        var i, r = [], c, x;
        for (i = 0; i < s.length; i++)
            if ((c = s.charCodeAt(i)) < 0x80) r.push(c);
            else if (c < 0x800) r.push(0xC0 + (c >> 6 & 0x1F), 0x80 + (c & 0x3F));
            else {
                if ((x = c ^ 0xD800) >> 10 == 0) //对四字节UTF-16转换为Unicode
                    c = (x << 10) + (s.charCodeAt(++i) ^ 0xDC00) + 0x10000,
                        r.push(0xF0 + (c >> 18 & 0x7), 0x80 + (c >> 12 & 0x3F));
                else r.push(0xE0 + (c >> 12 & 0xF));
                r.push(0x80 + (c >> 6 & 0x3F), 0x80 + (c & 0x3F));
            };
        return r;
    };

    util.sha1 = function(s) {
        var data = new Uint8Array(encodeUTF8(s))
        var i, j, t;
        var l = ((data.length + 8) >>> 6 << 4) + 16, s = new Uint8Array(l << 2);
        s.set(new Uint8Array(data.buffer)), s = new Uint32Array(s.buffer);
        for (t = new DataView(s.buffer), i = 0; i < l; i++)s[i] = t.getUint32(i << 2);
        s[data.length >> 2] |= 0x80 << (24 - (data.length & 3) * 8);
        s[l - 1] = data.length << 3;
        var w = [], f = [
                function () { return m[1] & m[2] | ~m[1] & m[3]; },
                function () { return m[1] ^ m[2] ^ m[3]; },
                function () { return m[1] & m[2] | m[1] & m[3] | m[2] & m[3]; },
                function () { return m[1] ^ m[2] ^ m[3]; }
            ], rol = function (n, c) { return n << c | n >>> (32 - c); },
            k = [1518500249, 1859775393, -1894007588, -899497514],
            m = [1732584193, -271733879, null, null, -1009589776];
        m[2] = ~m[0], m[3] = ~m[1];
        for (i = 0; i < s.length; i += 16) {
            var o = m.slice(0);
            for (j = 0; j < 80; j++)
                w[j] = j < 16 ? s[i + j] : rol(w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16], 1),
                    t = rol(m[0], 5) + f[j / 20 | 0]() + m[4] + w[j] + k[j / 20 | 0] | 0,
                    m[1] = rol(m[1], 30), m.pop(), m.unshift(t);
            for (j = 0; j < 5; j++)m[j] = m[j] + o[j] | 0;
        };
        t = new DataView(new Uint32Array(m).buffer);
        for (var i = 0; i < 5; i++)m[i] = t.getUint32(i << 2);

        var hex = Array.prototype.map.call(new Uint8Array(new Uint32Array(m).buffer), function (e) {
            return (e < 16 ? "0" : "") + e.toString(16);
        }).join("");
        return hex;
    }

    util.uuid = function() {
        var s = [];
        var hexDigits = "0123456789abcdef";
        for (var i = 0; i < 36; i++) {
            s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1);
        }
        s[14] = "4";  // bits 12-15 of the time_hi_and_version field to 0010
        s[19] = hexDigits.substr((s[19] & 0x3) | 0x8, 1);  // bits 6-7 of the clock_seq_hi_and_reserved to 01
        s[8] = s[13] = s[18] = s[23] = "-";

        var uuid = s.join("");
        return uuid;
    }

    util.ticket = function(type) {
        $.get(ctx + "moreway/v1/ticket", {"type": type}, function (data) {
            console.log("ticket: " + data.jsTicket);
            var timestamp = Date.parse(new Date());
            timestamp = timestamp / 1000;
            var u = util.uuid();
            var l = location.href.split('#')[0];
            var stringTemp = "jsapi_ticket=" + data.jsTicket +
                "&noncestr=" + u +
                "&timestamp=" + timestamp +
                "&url=" + l;
            var s = util.sha1(stringTemp);
            wx.config({
                debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
                appId: data.appId, // 必填，公众号的唯一标识
                timestamp: timestamp, // 必填，生成签名的时间戳
                nonceStr: u, // 必填，生成签名的随机串
                signature: s,// 必填，签名，见附录1
                jsApiList: ['onMenuShareTimeline','onMenuShareAppMessage','onMenuShareQQ','chooseWXPay','addCard','chooseCard','openCard','openLocation','getLocation']
            });
        });
    }

}($, window.util = {}));


