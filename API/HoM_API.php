<?php
$dir = "init";
$dest = "init";
$FILE = "init";
$response = "init";

$tempData = array();

/*
Parameter : String(저장 경로)
Return : None
Description : 파일 저장 경로 설정
*/
function dir_init($string){
	$GLOBALS['dir'] = $string;
}

/*
Parameter : String(저장 할 파일 이름)
Return : None
Description : 저장 될 파일 이름 설정
*/
function dest_init($string){
	$GLOBALS['dest'] = $string;
	$GLOBALS['dir'] = $GLOBALS['dir'].$GLOBALS['dest'];
}

/*
Parameter : Object(파일)
Return : None
Description : 파일 객체 설정
*/

function file_init($object){
	$GLOBALS['FILE'] = $object;
}

/*
Parameter : None
Return : success or failer
Description : 파일 객체를 이용해 설정된 주소와 파일 이름을 통해서 저장 
*/

function save_file(){
	if(move_uploaded_file($GLOBALS['FILE'],$GLOBALS['dir'])){
		return "success";
	}else{
		return "failer";
	}
}

/*
Parameter : None
Return : Object(All Data)
Description : 파일안에 존재하는 모든 데이터를 분리하고, 배열을 통해서 반환
*/
function parsing(){

	$GLOBALS['response'] = file_get_contents($GLOBALS['dir']) or die("Error : Cannot create file");
	$object = simplexml_load_string($GLOBALS['response']) or die("Error : Cannot create object");
	
	return $object;
}

/*
Parameter : Double(위도), Double(경도), String(마크 이름), String(이동 할 도메인 주소), String(마크 색상)
Return : None
Description : 입력된 데이터를 바탕으로 구글 맵에 마크를 구현한다.
*/

function add_data($lat, $lng, $name, $url, $color){
	array_push($GLOBALS['tempData'], array("latitude"=>$lat,"longitude"=>$lng,"name"=>$name,"directUrl"=>$url,"color"=>"http://maps.google.com/mapfiles/ms/icons/".$color."-dot.png"));
}


?>