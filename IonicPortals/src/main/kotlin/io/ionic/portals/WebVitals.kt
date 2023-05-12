package io.ionic.portals

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.getcapacitor.*
import com.getcapacitor.annotation.CapacitorPlugin
import org.json.JSONObject

/**
 * A class providing Web Vitals functionality. When Web Vitals metrics are desired, this class adds
 * JavaScript to the Portals web view to support measuring the performance of the web application.
 *
 * @link https://web.dev/vitals/
 * @property callback a callback to act on reported Web Vitals data
 */
@CapacitorPlugin(name = "WebVitals")
class WebVitals(val callback: (String, Metric, Long) -> Unit): Plugin() {
    override fun load() {
        bridge.webView.addJavascriptInterface(this, "WebVitals")
        bridge.webViewClient = object: BridgeWebViewClient(bridge) {
            override fun onPageFinished(view: WebView?, url: String?) {
                view?.loadUrl("javascript: $js")
                super.onPageFinished(view, url)
            }
        }
    }

    /**
     * Metrics supported by Portals Web Vitals.
     *
     * @link https://web.dev/vitals/
     */
    enum class Metric {
        /**
         * [First Contentful Paint](https://web.dev/fcp/)
         */
        FCP,

        /**
         * [First Input Delay](https://web.dev/fid/)
         */
        FID,

        /**
         * [Time to First Byte](https://web.dev/ttfb/)
         */
        TTFB
    }

    /**
     * Web Vitals handler script.
     * Built with the following:
     * web-vitals@3.1.0
     * esbuild@0.15.18
     *
     * Original script contains:
     * ```
     * import { onFCP, onTTFB, onFID } from "web-vitals";
     * const portalName = JSON.parse(AndroidInitialContext.initialContext()).name;
     * onFCP(report => WebVitals.fcp(portalName, report.value));
     * onTTFB(report => WebVitals.ttfb(portalName, report.value));
     * onFID(report => WebVitals.fid(portalName, report.value));
     * ```
     *
     * Build command:
     * esbuild ./index.js --bundle --minify --tree-shaking=true --platform=browser --outfile=dist/index.js
     */
    val js = """
        (()=>{var f,m,F,h;var I=-1,y=function(t){addEventListener("pageshow",function(e){e.persisted&&(I=e.timeStamp,t(e))},!0)},T=function(){return window.performance&&performance.getEntriesByType&&performance.getEntriesByType("navigation")[0]},E=function(){var t=T();return t&&t.activationStart||0},l=function(t,e){var i=T(),n="navigate";return I>=0?n="back-forward-cache":i&&(n=document.prerendering||E()>0?"prerender":document.wasDiscarded?"restore":i.type.replace(/_/g,"-")),{name:t,value:e===void 0?-1:e,rating:"good",delta:0,entries:[],id:"v3-".concat(Date.now(),"-").concat(Math.floor(8999999999999*Math.random())+1e12),navigationType:n}},P=function(t,e,i){try{if(PerformanceObserver.supportedEntryTypes.includes(t)){var n=new PerformanceObserver(function(a){Promise.resolve().then(function(){e(a.getEntries())})});return n.observe(Object.assign({type:t,buffered:!0},i||{})),n}}catch{}},N=function(t,e){var i=function n(a){a.type!=="pagehide"&&document.visibilityState!=="hidden"||(t(a),e&&(removeEventListener("visibilitychange",n,!0),removeEventListener("pagehide",n,!0)))};addEventListener("visibilitychange",i,!0),addEventListener("pagehide",i,!0)},v=function(t,e,i,n){var a,r;return function(o){e.value>=0&&(o||n)&&((r=e.value-(a||0))||a===void 0)&&(a=e.value,e.delta=r,e.rating=function(u,c){return u>c[1]?"poor":u>c[0]?"needs-improvement":"good"}(e.value,i),t(e))}},R=function(t){requestAnimationFrame(function(){return requestAnimationFrame(function(){return t()})})},C=function(t){document.prerendering?addEventListener("prerenderingchange",function(){return t()},!0):t()},d=-1,b=function(){return document.visibilityState!=="hidden"||document.prerendering?1/0:0},g=function(t){document.visibilityState==="hidden"&&d>-1&&(d=t.type==="visibilitychange"?t.timeStamp:0,H())},S=function(){addEventListener("visibilitychange",g,!0),addEventListener("prerenderingchange",g,!0)},H=function(){removeEventListener("visibilitychange",g,!0),removeEventListener("prerenderingchange",g,!0)},A=function(){return d<0&&(d=b(),S(),y(function(){setTimeout(function(){d=b(),S()},0)})),{get firstHiddenTime(){return d}}},D=function(t,e){e=e||{},C(function(){var i,n=[1800,3e3],a=A(),r=l("FCP"),o=P("paint",function(u){u.forEach(function(c){c.name==="first-contentful-paint"&&(o.disconnect(),c.startTime<a.firstHiddenTime&&(r.value=Math.max(c.startTime-E(),0),r.entries.push(c),i(!0)))})});o&&(i=v(t,r,n,e.reportAllChanges),y(function(u){r=l("FCP"),i=v(t,r,n,e.reportAllChanges),R(function(){r.value=performance.now()-u.timeStamp,i(!0)})}))})};var p={passive:!0,capture:!0},O=new Date,w=function(t,e){f||(f=e,m=t,F=new Date,k(removeEventListener),M())},M=function(){if(m>=0&&m<F-O){var t={entryType:"first-input",name:f.type,target:f.target,cancelable:f.cancelable,startTime:f.timeStamp,processingStart:f.timeStamp+m};h.forEach(function(e){e(t)}),h=[]}},q=function(t){if(t.cancelable){var e=(t.timeStamp>1e12?new Date:performance.now())-t.timeStamp;t.type=="pointerdown"?function(i,n){var a=function(){w(i,n),o()},r=function(){o()},o=function(){removeEventListener("pointerup",a,p),removeEventListener("pointercancel",r,p)};addEventListener("pointerup",a,p),addEventListener("pointercancel",r,p)}(e,t):w(e,t)}},k=function(t){["mousedown","keydown","touchstart","pointerdown"].forEach(function(e){return t(e,q,p)})},x=function(t,e){e=e||{},C(function(){var i,n=[100,300],a=A(),r=l("FID"),o=function(s){s.startTime<a.firstHiddenTime&&(r.value=s.processingStart-s.startTime,r.entries.push(s),i(!0))},u=function(s){s.forEach(o)},c=P("first-input",u);i=v(t,r,n,e.reportAllChanges),c&&N(function(){u(c.takeRecords()),c.disconnect()},!0),c&&y(function(){var s;r=l("FID"),i=v(t,r,n,e.reportAllChanges),h=[],m=-1,f=null,k(addEventListener),s=o,h.push(s),M()})})};var W=1/0;var V=function t(e){document.prerendering?C(function(){return t(e)}):document.readyState!=="complete"?addEventListener("load",function(){return t(e)},!0):setTimeout(e,0)},B=function(t,e){e=e||{};var i=[800,1800],n=l("TTFB"),a=v(t,n,i,e.reportAllChanges);V(function(){var r=T();if(r){var o=r.responseStart;if(o<=0||o>performance.now())return;n.value=Math.max(o-E(),0),n.entries=[r],a(!0),y(function(){n=l("TTFB",0),(a=v(t,n,i,e.reportAllChanges))(!0)})}})};var L=JSON.parse(AndroidInitialContext.initialContext()).name;D(t=>WebVitals.fcp(L,t.value));B(t=>WebVitals.ttfb(L,t.value));x(t=>WebVitals.fid(L,t.value));})();
    """.trimIndent()

    /**
     * First Contentful Paint (FCP)
     * @link https://web.dev/fcp/
     * @param time Time in milliseconds when FCP is measured
     */
    @JavascriptInterface
    fun fcp(portalName: String, time: Long) {
        callback(portalName, Metric.FCP, time)
    }

    /**
     * First Input Delay (FID)
     * @link https://web.dev/fid/
     * @param time Time in milliseconds when FID is measured
     */
    @JavascriptInterface
    fun fid(portalName: String, time: Long) {
        callback(portalName, Metric.FID, time)
    }

    /**
     * Time To First Byte (TTFB)
     * @link https://web.dev/ttfb/
     * @param time Time in milliseconds when TTFB is measured
     */
    @JavascriptInterface
    fun ttfb(portalName: String, time: Long) {
        callback(portalName, Metric.TTFB, time)
    }
}