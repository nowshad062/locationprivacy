<html>
    <head>
  <title>Admin Portal</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.2/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="jquery-latest.js"></script> 
  <script type="text/javascript" src="jquery.tablesorter.js"></script>
    <script src="sorttable.js"></script>
        <style>
        #map {
        height: 45%;
      }
            #userid{
                text-transform:capitalize;
            }
            table.sortable thead {
    background-color:#eee;
    color:#666666;
    font-weight: bold;
    cursor: default;
}
            
        </style>
        <script>
            var user = "";
            var date1 = "";
            var date2 = "";
            var time1 = "";
            var time2 = "";
            var color;
            var listvalues = new Array();
            var set = new Set();
            var tr;
            var i, ij;
            var list;
            var newTableObject;
            //var o;
            
            $(document).ready(function(){
                //$('#tableid').DataTable();
              //  o=0;
                newTableObject = document.getElementById("tableid");
                list = document.getElementById("names");
                document.getElementById("map").style.visibility = "hidden";
                document.getElementById("heading").style.visibility = "hidden";
                document.getElementById("names").style.visibility = "hidden";
                document.getElementById("trace").style.visibility = "hidden";
                var table = document.getElementById("tableid");
            while(table.rows.length > 1) {
  table.deleteRow(1);
}
     var obj = $.getJSON("../search.php?username="+user+"&stime="+time1+"&etime="+time2+"&sdate="+date1+"&edate="+date2, function(data){
            $.each(data, function(i, field){
                if(i=="record"){
                    var tr;
                    //listvalues = listvalues.concat('[');
    for (var ij = 0; ij < field.length; ij++) {
        tr = $('<tr/>');
        tr.append("<td id="+"userid"+ij+">" + field[ij].username + "</td>");
        tr.append("<td id="+"userlat"+ij+">" + field[ij].curlat + "</td>");
        tr.append("<td id="+"userlon"+ij+">" + field[ij].curlon + "</td>");
        tr.append("<td id="+"usertime"+ij+">" + field[ij].reqtime + "</td>");
        $('#tableid').append(tr);
        }           
                }
                
            
                });           
                
                   
            });
                
                var obj1 = $.getJSON("../users.php", function(data){
            $.each(data, function(i, field){
                if(i=="record"){
                    
                    var option;
                    //listvalues = listvalues.concat('[');
    for (var ij1 = 0; ij1 < field.length; ij1++) {
        
        option = $('<option/>');
        option.append(field[ij1].username);
        $('#listid').append(option);
        }           
                }
                
            
                });           
                
                   
            });
                sorttable.makeSortable(newTableObject);
                 
        });
            
                
            var count = 0;            
        function results(){
            document.getElementById("map").style.visibility = "hidden";
            document.getElementById("names").style.visibility = "hidden";
            document.getElementById("heading").style.visibility = "hidden";
            document.getElementById("trace").style.visibility = "visible";
             user = document.getElementById("username").value;
             date1 = document.getElementById("date1").value;
            date2 = document.getElementById("date2").value;
            if(count==0){
                var table = document.getElementById("tableid");
            while(table.rows.length > 1) {
  table.deleteRow(1);
}
     var obj = $.getJSON("../search.php?username="+user+"&stime="+time1+"&etime="+time2+"&sdate="+date1+"&edate="+date2, function(data){
            $.each(data, function(i, field){
                if(i=="record"){
                    
    for (ij = 0; ij < field.length; ij++) {
        tr = $('<tr/>');
        tr.append("<td id="+"userid"+ij+">" + field[ij].username + "</td>");
        tr.append("<td id="+"userlat"+ij+">" + field[ij].curlat + "</td>");
        tr.append("<td id="+"userlon"+ij+">" + field[ij].curlon + "</td>");
        tr.append("<td id="+"usertime"+ij+">" + field[ij].reqtime + "</td>");
        $('#tableid').append(tr);
        set.add(field[ij].username);
        
    }           //count++;   
                }
                for(var v of set){
                if(i==v)                    
                    listvalues = field;
                }
            });
        });
    
    initMap();
    }
    }
            
            
            function initMap() {
                var o = 0;
                while (list.hasChildNodes()) {
                    list.removeChild(list.firstChild);
                        }
                document.getElementById("map").style.visibility = "hidden";
                document.getElementById("names").style.visibility = "hidden";
                document.getElementById("heading").style.visibility = "hidden";
                //document.getElementById("trace").style.visibility = "hidden";
          var map = new google.maps.Map(document.getElementById('map'), {
          zoom: 8,
          center: listvalues[0],
          mapTypeId: google.maps.MapTypeId.TERRAIN
        });
        /*var json=[{lat: 30.0873, lng: -95.9913},
                  {lat: 30.0873, lng: -95.9913},
                  {lat: 30.0873, lng: -95.9913},
                  {lat: 30.0973, lng: -95.9898},
                  {lat: 30.0987, lng: -95.6193}];
          */
                var obj = $.getJSON("../search.php?username="+user+"&stime="+time1+"&etime="+time2+"&sdate="+date1+"&edate="+date2, function(data){
            $.each(data, function(i, field){
                if(i=="record"){
                    
    for (ij = 0; ij < field.length; ij++) {
        set.add(field[ij].username);
        
    }           //count++;   
                }
                for(var v of set){
                if(i==v) 
                    {
                    listvalues = field;
                    color = getRandomColor();
                    if(o < set.size)
                    {
                    var para = document.createElement("p");
                    var node = document.createTextNode(v);
                    para.appendChild(node);
                    para.style.color = color;
                    var element = document.getElementById("names");
                    element.appendChild(para);
                    o++;
                    }
                    var json = listvalues;
        var flightPlanCoordinates = json;
          var flightPath = new google.maps.Polyline({
          path: flightPlanCoordinates,
          geodesic: true,
          strokeColor: color,
          strokeOpacity: 1.0,
          strokeWeight: 2
        });
                    for(var k=0;k<listvalues.length;k++){
                 marker = new google.maps.Marker({
            map: map,
            icon: {
      path: google.maps.SymbolPath.BACKWARD_CLOSED_ARROW,
      scale: 4,
                
                strokeColor: color,
                strokeOpacity: 1,
                strokeWeight: 2
               },        
                title: "Username="+v+" Position="+listvalues[k].lat+","+listvalues[k].lng+", Req Time="+listvalues[k].reqtime,
                position: new google.maps.LatLng(listvalues[k].lat, listvalues[k].lng)
                
        });  
                               
}
                        
        flightPath.setMap(map);
                }
                    
            }
                
            });
        });
          
                
                //document.getElementById("trace").style.visibility = "hidden";
          
      }
            
            function s(){
                document.getElementById("map").style.visibility = "visible";
                document.getElementById("names").style.visibility = "visible";
                document.getElementById("heading").style.visibility = "visible";
                document.getElementById("trace").style.visibility = "hidden";
            }
            
            function getRandomColor() {
    var letters = '0123456789ABCDEF'.split('');
    var color = '#';
    for (var i = 0; i < 6; i++ ) {
        color += letters[Math.floor(Math.random() * 16)];
    }
    return color;
}
            
            
        </script>
</head>
   <body>
    <div>
    <h1>Welcome to our Location Based Service</h1>
    </div>
<div class="container special">
       
    <div class="row">
    <div class="col-md-4">
        <label class="col-sm-2 control-label">Search</label>
        <input class="form-control" id="username" type="text" autocomplete="off" placeholder="Search here" list="listid">
        <datalist id="listid">
        
         </datalist>
        </div>
        <div class="col-md-4">
            <div class="col-sm-8">
        <label class="col-sm-8 control-label">Date filter</label>
                </div>
            <div class='col-md-6'>
        <div class="form-group">
            <div class="date" id='datetimepicker6'>
                <input type='date' id="date1" class="form-control" placeholder="yyyy-mm-dd"/>
                </div>
        </div>
    </div>
    <div class='col-md-6'>
        <div class="form-group">
            <div class="date" id='datetimepicker7'>
                <input type="date" id="date2" class="form-control" placeholder="yyyy-mm-dd"/>
                </div>
        </div>
    </div>
            </div>
        
        <div align = "center" class="col-md-4">
            <br>
        <button onclick="results()" type="button" class="btn btn-primary">Search</button>
        <button onclick="results(); s();" type="button" class="btn btn-primary" id="trace">Trace on map</button>    
        </div>
        
    </div>
    <br>
    
    <div style="
                overflow-y: auto;
                max-height:620px;">    
    <table class="sortable table table-bordered col-md-12" id="tableid">
    <thead>
      
        <th>Username</th>
        <th>Lattitude</th>
        <th>Longitude</th>
          <th>Request Time</th>
      
    </thead>
        <tr>
        </tr>
      
  </table></div>
    </div>    
       <br>
    
       <div class="container">
           <div class="side">
       <div id="map" style="width:1050px; float:left; border:2px ">
    </div>
               <div id="heading" style="margin-left:20px">
                   <h5>Traced Users: </h5>
               </div>
           <div id="names" style="float:left; margin-left:20px">
           </div>
    </div>
           </div>
    <script>

      // This example creates a 2-pixel-wide red polyline showing the path of William
      // Kingsford Smith's first trans-Pacific flight between Oakland, CA, and
      // Brisbane, Australia.

      
    </script>
    <script async defer
    src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCFLGY58ZN5ESpLddviG9o_9TMjkLbWiis&callback=initMap">
    </script>
</body>
</html>