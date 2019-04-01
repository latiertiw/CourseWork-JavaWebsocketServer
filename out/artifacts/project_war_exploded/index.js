var startDate = new Date();
setInterval(function(){
    document.getElementById("out").innerHTML = ((new Date()-startDate)/1000)+" секунд";
},50)