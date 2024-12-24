import{_,r as i,o as C,u as L,a as n,c as u,b as o,t as M,w as $,d as w,v as I,e as V,F as B,f as S,g as T,h as N}from"./index-B4jUAWKv.js";import{a as f,B as p}from"./constants-C7wq7-ly.js";const U={key:0},h={class:"book"},R={key:0,class:"book-info"},A={class:"title"},K={key:1},O={key:2},P={__name:"BookCell",props:["bookId"],emits:["deletedBook"],setup(g,{emit:r}){const d=g,m=r,s=i({id:null,title:"",startParaId:null}),v=i(!0),y=i(!1),k=i(!1),c=i("");C(async()=>{try{const l=await f.get(`${p}/book/${d.bookId}`);l.data.success?(console.log("Book loaded successfully!"),s.value=l.data.data,v.value=!1,y.value=!0):(console.log("Failed to load book:",l.data.errorMsg),v.value=!1)}catch(l){console.error("Error loading book:",l),v.value=!1}});const a=L(),t=()=>{a.push({name:"BookContent",params:{bookId:s.value.id}})},b=()=>{c.value=s.value.title,k.value=!0},x=async()=>{try{s.value.title=c.value;const l=await f.put(`${p}/book/${s.value.id}`,{title:c.value});l.data.success?(console.log("Book updated successfully!"),console.log(l.data.data),k.value=!1):console.log("Failed to update book:",l.data.errorMsg)}catch(l){console.error("Error submitting edit:",l)}},E=()=>{k.value=!1},D=async()=>{if(window.confirm(`你确定要删除《${s.value.title}》这本书吗？`))try{const e=await f.delete(`${p}/book/${s.value.id}`);e.data.success?(console.log("Book deleted successfully!"),console.log(e.data.data),m("deletedBook",s.value.id)):console.log("Failed to delete book:",e.data.errorMsg)}catch(e){console.error("Error deleting book:",e)}};return(l,e)=>y.value?(n(),u("div",U,[o("div",h,[k.value?(n(),u("form",{key:1,onSubmit:$(x,["prevent"])},[e[4]||(e[4]=o("br",null,null,-1)),e[5]||(e[5]=o("br",null,null,-1)),o("div",null,[e[2]||(e[2]=o("label",{for:"title"},"标题:",-1)),w(o("input",{"onUpdate:modelValue":e[0]||(e[0]=F=>c.value=F),type:"text",id:"title"},null,512),[[I,c.value]])]),o("div",{class:"buttons"},[o("button",{type:"button",onClick:E},"取消"),e[3]||(e[3]=o("button",{type:"submit"},"提交",-1))])],32)):(n(),u("div",R,[o("h1",A,M(s.value.title),1),o("div",{class:"buttons"},[o("button",{onClick:t},"进入小说"),o("button",{onClick:b},"编辑信息"),o("button",{onClick:D},"删除")]),e[1]||(e[1]=o("p",null,[o("br")],-1))]))]),V(l.$slots,"default",{},void 0)])):v.value?(n(),u("div",K,"Loading...")):(n(),u("div",O,"Failed to load..."))}},j=_(P,[["__scopeId","data-v-92982096"]]),q={class:"main-info"},z={key:0,class:"buttons"},G={class:"books"},H={__name:"BookList",setup(g){const r=i([]),d=i(!1),m=i(!0),s=i("");C(async()=>{try{const a=await f.get(`${p}/bookIds`);r.value=a.data.data,m.value=!0}catch(a){console.log(a)}finally{m.value=!1}});function v(a){r.value=r.value.filter(t=>t!==a)}function y(){s.value="",d.value=!0}async function k(){try{const a=await f.post(`${p}/book`,{title:s.value});a.data.success&&(r.value.push(a.data.data),d.value=!1)}catch(a){console.log(a)}}function c(){d.value=!1}return(a,t)=>(n(),u(B,null,[o("div",q,[t[5]||(t[5]=o("h1",null,"小说接龙",-1)),d.value?(n(),u("form",{key:1,onSubmit:$(k,["prevent"])},[t[3]||(t[3]=o("br",null,null,-1)),t[4]||(t[4]=o("br",null,null,-1)),o("div",null,[t[1]||(t[1]=o("label",{for:"title"},"标题:",-1)),w(o("input",{"onUpdate:modelValue":t[0]||(t[0]=b=>s.value=b),type:"text",id:"title"},null,512),[[I,s.value]])]),o("div",{class:"buttons"},[o("button",{type:"button",onClick:c},"取消"),t[2]||(t[2]=o("button",{type:"submit"},"提交",-1))])],32)):(n(),u("div",z,[o("button",{onClick:y},"新建小说")]))]),o("div",G,[(n(!0),u(B,null,S(r.value,b=>(n(),T(j,{key:b,bookId:b,onDeletedBook:v},null,8,["bookId"]))),128))])],64))}},J=_(H,[["__scopeId","data-v-1f5aad75"]]),X={__name:"BookListView",setup(g){return(r,d)=>(n(),u("div",null,[N(J)]))}};export{X as default};
