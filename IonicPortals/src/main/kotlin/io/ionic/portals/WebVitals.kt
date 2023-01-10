package io.ionic.portals

import android.webkit.JavascriptInterface

class WebVitals(val portalName: String, val callback: (Metric, Long) -> Unit) {

    enum class Metric {
        FCP, FID, TTFB
    }

    val js = """
        (()=>{var f,m,w,h;var F=-1,y=function(t){addEventListener("pageshow",function(e){e.persisted&&(F=e.timeStamp,t(e))},!0)},T=function(){return window.performance&&performance.getEntriesByType&&performance.getEntriesByType("navigation")[0]},E=function(){var t=T();return t&&t.activationStart||0},l=function(t,e){var i=T(),n="navigate";return F>=0?n="back-forward-cache":i&&(n=document.prerendering||E()>0?"prerender":document.wasDiscarded?"restore":i.type.replace(/_/g,"-")),{name:t,value:e===void 0?-1:e,rating:"good",delta:0,entries:[],id:"v3-".concat(Date.now(),"-").concat(Math.floor(8999999999999*Math.random())+1e12),navigationType:n}},I=function(t,e,i){try{if(PerformanceObserver.supportedEntryTypes.includes(t)){var n=new PerformanceObserver(function(a){Promise.resolve().then(function(){e(a.getEntries())})});return n.observe(Object.assign({type:t,buffered:!0},i||{})),n}}catch{}},x=function(t,e){var i=function n(a){a.type!=="pagehide"&&document.visibilityState!=="hidden"||(t(a),e&&(removeEventListener("visibilitychange",n,!0),removeEventListener("pagehide",n,!0)))};addEventListener("visibilitychange",i,!0),addEventListener("pagehide",i,!0)},v=function(t,e,i,n){var a,r;return function(o){e.value>=0&&(o||n)&&((r=e.value-(a||0))||a===void 0)&&(a=e.value,e.delta=r,e.rating=function(u,c){return u>c[1]?"poor":u>c[0]?"needs-improvement":"good"}(e.value,i),t(e))}},R=function(t){requestAnimationFrame(function(){return requestAnimationFrame(function(){return t()})})},C=function(t){document.prerendering?addEventListener("prerenderingchange",function(){return t()},!0):t()},d=-1,L=function(){return document.visibilityState!=="hidden"||document.prerendering?1/0:0},g=function(t){document.visibilityState==="hidden"&&d>-1&&(d=t.type==="visibilitychange"?t.timeStamp:0,H())},b=function(){addEventListener("visibilitychange",g,!0),addEventListener("prerenderingchange",g,!0)},H=function(){removeEventListener("visibilitychange",g,!0),removeEventListener("prerenderingchange",g,!0)},P=function(){return d<0&&(d=L(),b(),y(function(){setTimeout(function(){d=L(),b()},0)})),{get firstHiddenTime(){return d}}},A=function(t,e){e=e||{},C(function(){var i,n=[1800,3e3],a=P(),r=l("FCP"),o=I("paint",function(u){u.forEach(function(c){c.name==="first-contentful-paint"&&(o.disconnect(),c.startTime<a.firstHiddenTime&&(r.value=Math.max(c.startTime-E(),0),r.entries.push(c),i(!0)))})});o&&(i=v(t,r,n,e.reportAllChanges),y(function(u){r=l("FCP"),i=v(t,r,n,e.reportAllChanges),R(function(){r.value=performance.now()-u.timeStamp,i(!0)})}))})};var p={passive:!0,capture:!0},N=new Date,S=function(t,e){f||(f=e,m=t,w=new Date,M(removeEventListener),D())},D=function(){if(m>=0&&m<w-N){var t={entryType:"first-input",name:f.type,target:f.target,cancelable:f.cancelable,startTime:f.timeStamp,processingStart:f.timeStamp+m};h.forEach(function(e){e(t)}),h=[]}},O=function(t){if(t.cancelable){var e=(t.timeStamp>1e12?new Date:performance.now())-t.timeStamp;t.type=="pointerdown"?function(i,n){var a=function(){S(i,n),o()},r=function(){o()},o=function(){removeEventListener("pointerup",a,p),removeEventListener("pointercancel",r,p)};addEventListener("pointerup",a,p),addEventListener("pointercancel",r,p)}(e,t):S(e,t)}},M=function(t){["mousedown","keydown","touchstart","pointerdown"].forEach(function(e){return t(e,O,p)})},k=function(t,e){e=e||{},C(function(){var i,n=[100,300],a=P(),r=l("FID"),o=function(s){s.startTime<a.firstHiddenTime&&(r.value=s.processingStart-s.startTime,r.entries.push(s),i(!0))},u=function(s){s.forEach(o)},c=I("first-input",u);i=v(t,r,n,e.reportAllChanges),c&&x(function(){u(c.takeRecords()),c.disconnect()},!0),c&&y(function(){var s;r=l("FID"),i=v(t,r,n,e.reportAllChanges),h=[],m=-1,f=null,M(addEventListener),s=o,h.push(s),D()})})};var V=1/0;var q=function t(e){document.prerendering?C(function(){return t(e)}):document.readyState!=="complete"?addEventListener("load",function(){return t(e)},!0):setTimeout(e,0)},B=function(t,e){e=e||{};var i=[800,1800],n=l("TTFB"),a=v(t,n,i,e.reportAllChanges);q(function(){var r=T();if(r){var o=r.responseStart;if(o<=0||o>performance.now())return;n.value=Math.max(o-E(),0),n.entries=[r],a(!0),y(function(){n=l("TTFB",0),(a=v(t,n,i,e.reportAllChanges))(!0)})}})};A(t=>WebVitals.fcp(t.value));k(t=>WebVitals.fid(t.value));B(t=>WebVitals.ttfb(t.value));})();
    """.trimIndent()

    @JavascriptInterface
    fun fcp(time: Long) {
        callback(Metric.FCP, time)
    }

    @JavascriptInterface
    fun fid(time: Long) {
        callback(Metric.FID, time)
    }

    @JavascriptInterface
    fun ttfb(time: Long) {
        callback(Metric.TTFB, time)
    }

}