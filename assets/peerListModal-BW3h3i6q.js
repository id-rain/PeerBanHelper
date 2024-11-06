import{s as z,i as O,f as m,I as U,b as q,c as E,_ as J}from"./index-B0_Z7lwl.js";import{_ as K}from"./queryIpLink.vue_vue_type_script_setup_true_lang-HZkvcNLr.js";import{f as R,a6 as G,r as g,a8 as H,j as _,s as h,t as e,v as p,x as o,a2 as l,y as a,u as N,p as Q,k,F as W,P as X}from"./libs-D-uoNQCp.js";import{T as Y,S as Z,K as ee,c as ae,t as te,J as se}from"./arcoDesign-CRyOpHTj.js";const oe=R({__name:"peerListModal",setup(le,{expose:D}){const{t:n}=G(),f=g(!1),w=g(""),v=g(""),b=g("");D({showModal:(i,s,r)=>{w.value=i,v.value=s,b.value=r,f.value=!0,C(i,s)}});const S=()=>{f.value=!1,w.value="",v.value=""},{data:y,loading:T,run:C,cancel:F}=H(z,{defaultParams:[w.value,v.value],manual:!0,pollingInterval:1e3}),P=[{title:()=>n("page.dashboard.peerList.column.address"),slotName:"peerAddress",width:320},{title:()=>n("page.dashboard.peerList.column.flag"),slotName:"flags",width:110},{title:"Peer ID",dataIndex:"peer.id",width:100},{title:()=>n("page.dashboard.peerList.column.clientName"),dataIndex:"peer.clientName",width:300},{title:()=>n("page.dashboard.peerList.column.speed"),slotName:"speed",width:140},{title:()=>n("page.dashboard.peerList.column.uploadedDownloaded"),slotName:"uploadDownload",width:140},{title:()=>n("page.dashboard.peerList.column.progress"),slotName:"progress",width:100}],A=i=>i.split(" ").map(s=>s+" - "+n("page.dashboard.peerList.column.flags."+s.trim()));return(i,s)=>{const r=Y,d=Z,x=U,L=q,B=ee,M=E,V=ae,j=te,$=se;return _(),h($,{visible:f.value,"onUpdate:visible":s[0]||(s[0]=c=>f.value=c),"hide-cancel":"",closable:"","unmount-on-close":"",width:"auto",onOk:S,onClose:s[1]||(s[1]=c=>l(F)())},{title:e(()=>[p(o(l(n)("page.dashboard.peerList.title")+b.value),1)]),default:e(()=>{var c;return[a(j,{columns:P,data:(c=l(y))==null?void 0:c.data,loading:!l(T)&&!l(y),style:{width:"1600px"},"virtual-list-props":{height:500},pagination:!1},{peerAddress:e(({record:t})=>[a(d,{wrap:!1},{default:e(()=>{var u,I;return[(I=(u=t.geo)==null?void 0:u.country)!=null&&I.iso?(_(),h(O,{key:0,iso:t.geo.country.iso},null,8,["iso"])):N("",!0),a(r,{copyable:"",code:"",style:{"white-space":"nowrap"}},{default:e(()=>[a(K,{ip:t.peer.address.ip,style:{color:"var(--color-text-2)"}},{default:e(()=>[p(o(t.peer.address.ip)+":"+o(t.peer.address.port),1)]),_:2},1032,["ip"])]),_:2},1024)]}),_:2},1024)]),speed:e(({record:t})=>[a(d,{fill:"",style:{"justify-content":"space-between"}},{default:e(()=>[a(d,{fill:"",direction:"vertical"},{default:e(()=>[a(r,null,{default:e(()=>[a(x,{class:"green"}),p(" "+o(l(m)(t.peer.uploadSpeed))+"/s ",1)]),_:2},1024),a(r,null,{default:e(()=>[a(L,{class:"red"}),p(" "+o(l(m)(t.peer.downloadSpeed))+"/s ",1)]),_:2},1024)]),_:2},1024)]),_:2},1024)]),uploadDownload:e(({record:t})=>[a(d,{fill:"",style:{"justify-content":"space-between"}},{default:e(()=>[a(d,{fill:"",direction:"vertical"},{default:e(()=>[a(r,null,{default:e(()=>[a(x,{class:"green"}),p(" "+o(l(m)(t.peer.uploaded)),1)]),_:2},1024),a(r,null,{default:e(()=>[a(L,{class:"red"}),p(" "+o(l(m)(t.peer.downloaded)),1)]),_:2},1024)]),_:2},1024)]),_:2},1024)]),progress:e(({record:t})=>[a(d,null,{default:e(()=>[a(B,{percent:t.peer.progress,size:"mini"},null,8,["percent"]),a(r,null,{default:e(()=>[p(o((t.peer.progress*100).toFixed(2)+"%"),1)]),_:2},1024)]),_:2},1024)]),flags:e(({record:t})=>[Q("p",null,[p(o(t.peer.flags)+" ",1),t.peer.flags?(_(),h(V,{key:0},{content:e(()=>[(_(!0),k(W,null,X(A(t.peer.flags),u=>(_(),k("p",{key:u},o(u),1))),128))]),default:e(()=>[a(M)]),_:2},1024)):N("",!0)])]),_:1},8,["data","loading"])]}),_:1},8,["visible"])}}}),ie=J(oe,[["__scopeId","data-v-953df596"]]);export{ie as default};