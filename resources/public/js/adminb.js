$(document).ready(function () {
               
      $('#myModal .dlbtn').click(function (event) {
        
      
          var beer = document.getElementById("beername").value;
            
          $.ajax({url: "/deleteBeer", data: {beername : beer}, type: 'POST', success: function(result){
                  alert(result);
                  window.location.reload();
                            
          }});   

      });
      
      $('#myModal .clsbtn').click(function (event) {
        
           document.getElementById("beername").value = "";

      });
      
});