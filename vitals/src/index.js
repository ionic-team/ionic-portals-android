import { onFCP, onTTFB, onFID } from "web-vitals";

const portalName = JSON.parse(AndroidInitialContext.initialContext()).name

onFCP(report => WebVitals.fcp(portalName, report.value));
onTTFB(report => WebVitals.ttfb(portalName, report.value));
onFID(report => WebVitals.fid(portalName, report.value));
