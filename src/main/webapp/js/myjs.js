/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function approveBilDetail(bill_detail_id) {
    var girdlayout = "." + bill_detail_id;
    $(girdlayout).css("background-color", "#0070c0");
    $(girdlayout).css("background", "url(images/approve.png)");
    $(girdlayout).css("background-repeat", "no-repeat");
    $(girdlayout).css("background-position", "center");
}


