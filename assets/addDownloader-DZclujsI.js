const __vite__mapDeps=(i,m=__vite__mapDeps,d=(m.f||(m.f=["./qbittorrent-BRZM0LpL.js","./index-BOOfbzia.js","./libs-D7QMUV-4.js","./arcoDesign-C4qBzdfG.js","./index-DRoH6C0v.css","./qbittorrentee-CPsHrIBx.js","./transmission-KGDYG8Y6.js","./biglybt-4CdECQbE.js","./deluge-CP1BjzLB.js"])))=>i.map(i=>d[i]);
import{C as l,_ as m}from"./index-BOOfbzia.js";import{T as k}from"./init-BEPw3HV9.js";import{f as L,a6 as N,am as H,r as S,q as U,j as c,s as y,t as e,y as t,v as d,x as _,a2 as n,E as j,p as G,O as Y,u as Z,a8 as p}from"./libs-D7QMUV-4.js";import{M as v,c as z,Z as J,Y as K,R as Q,T as W,o as X,F as $,p as ee,B as oe,q as te,S as ae}from"./arcoDesign-C4qBzdfG.js";const ne={href:"https://github.com/PBH-BTN/PBH-Adapter-BiglyBT"},ie=L({__name:"addDownloader",props:{modelValue:{required:!0},modelModifiers:{}},emits:["update:modelValue"],setup(b){const w=p(()=>m(()=>import("./qbittorrent-BRZM0LpL.js"),__vite__mapDeps([0,1,2,3,4]),import.meta.url)),T=p(()=>m(()=>import("./qbittorrentee-CPsHrIBx.js"),__vite__mapDeps([5,1,2,3,4]),import.meta.url)),B=p(()=>m(()=>import("./transmission-KGDYG8Y6.js"),__vite__mapDeps([6,1,2,3,4]),import.meta.url)),C=p(()=>m(()=>import("./biglybt-4CdECQbE.js"),__vite__mapDeps([7,1,2,3,4]),import.meta.url)),E=p(()=>m(()=>import("./deluge-CP1BjzLB.js"),__vite__mapDeps([8,1,2,3,4]),import.meta.url)),V={[l.qBittorrent]:w,[l.qBittorrentEE]:T,[l.Transmission]:B,[l.BiglyBT]:C,[l.Deluge]:E},{t:r}=N(),a=H(b,"modelValue"),f=S(!1),D=async()=>{f.value=!0;try{const s=await k({name:a.value.downloaderConfig.name,config:a.value.downloaderConfig.config});if(!s.success)throw new Error(s.message)}catch(s){return s instanceof Error&&v.error({content:s.message,resetOnHover:!0}),!1}finally{f.value=!1}v.success({content:r("page.oobe.addDownloader.test.success"),resetOnHover:!0}),a.value.valid=!0};return(s,o)=>{const q=z,h=J,M=K,i=Q,P=W,R=X,F=U("i18n-t"),g=$,I=ee,O=oe,x=te,A=ae;return c(),y(A,{direction:"vertical",style:{width:"70%"}},{default:e(()=>[t(M,{style:{"text-align":"left"}},{default:e(()=>[t(q,null,{default:e(()=>[d(_(n(r)("page.oobe.addDownloader.title")),1)]),_:1}),t(h,null,{default:e(()=>[d(_(n(r)("page.oobe.addDownloader.description")),1)]),_:1})]),_:1}),t(x,{model:a.value.downloaderConfig,"auto-label-width":""},{default:e(()=>[t(g,{field:"config.type",label:n(r)("page.dashboard.editModal.label.type"),required:""},j({default:e(()=>[t(R,{modelValue:a.value.downloaderConfig.config.type,"onUpdate:modelValue":o[0]||(o[0]=u=>a.value.downloaderConfig.config.type=u),type:"button",style:{overflow:"scroll","overflow-y":"hidden"}},{default:e(()=>[t(i,{value:n(l).qBittorrent},{default:e(()=>o[3]||(o[3]=[d("qBittorrent")])),_:1},8,["value"]),t(i,{value:n(l).qBittorrentEE},{default:e(()=>o[4]||(o[4]=[d("qBittorrentEE")])),_:1},8,["value"]),t(i,{value:n(l).BiglyBT},{default:e(()=>o[5]||(o[5]=[d("BiglyBT")])),_:1},8,["value"]),t(i,{value:n(l).Deluge},{default:e(()=>o[6]||(o[6]=[d("Deluge")])),_:1},8,["value"]),t(P,{content:n(r)("page.dashboard.editModal.transmission.discourage")},{default:e(()=>[t(i,{value:n(l).Transmission,disabled:""},{default:e(()=>o[7]||(o[7]=[d("Transmission")])),_:1},8,["value"])]),_:1},8,["content"])]),_:1},8,["modelValue"])]),_:2},[a.value.downloaderConfig.config.type===n(l).BiglyBT?{name:"extra",fn:e(()=>[t(F,{keypath:"page.dashboard.editModal.biglybt"},{url:e(()=>[G("a",ne,_(n(r)("page.dashboard.editModal.biglybt.url")),1)]),_:1})]),key:"0"}:void 0]),1032,["label"]),t(g,{field:"name",label:n(r)("page.dashboard.editModal.label.name"),required:""},{default:e(()=>[t(I,{modelValue:a.value.downloaderConfig.name,"onUpdate:modelValue":o[1]||(o[1]=u=>a.value.downloaderConfig.name=u),"allow-clear":""},null,8,["modelValue"])]),_:1},8,["label"]),(c(),y(Y(V[a.value.downloaderConfig.config.type]),{modelValue:a.value.downloaderConfig.config,"onUpdate:modelValue":o[2]||(o[2]=u=>a.value.downloaderConfig.config=u)},null,8,["modelValue"])),a.value.downloaderConfig.config.type?(c(),y(g,{key:0},{default:e(()=>[t(O,{loading:f.value,onClick:D},{default:e(()=>[d(_(n(r)("page.oobe.addDownloader.test")),1)]),_:1},8,["loading"])]),_:1})):Z("",!0)]),_:1},8,["model"])]),_:1})}}});export{ie as default};
