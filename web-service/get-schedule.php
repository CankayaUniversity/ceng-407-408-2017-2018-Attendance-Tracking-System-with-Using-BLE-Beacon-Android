<?php
require_once 'db.php';
$url = "http://www.cankaya.edu.tr/dersler/";
$start = 500;
$stop = 600;
$content = file_get_contents($url);
$abbreviation = array('ARCH', 'BAF', 'CE', 'CENG', 'CRP', 'ECE', 'ECON', 'EE', 'ELL', 'IE', 'INAR', 'INTT', 'MAN', 'MATH', 'ME', 'MECE', 'MSE', 'PSI', 'PSY', 'TINS');
$week_days = array('monday','tuesday','wednesday','thursday','friday','saturday','sunday');


$result = explode('<table class="table table-striped table-bordered table-hover">
		<thead>
		<tr>
			<th width="90"><b>Ders Kodu</b></th><th width="402"><b>Ders Adı</b></th><th width="187"><b>Haftalık Ders Programı</b></th><th width="90"><b>Sayfa Linki</b></th>
		</tr>
		</thead>
		<tr>
			<td>&nbsp;</td><td>WebOnline </td><td></td><td><a href="http://webonline.cankaya.edu.tr/">Sayfa Linki</a></td>
		</tr>
		<tr>
			<td>&nbsp;</td><td>Hazırlık Sınıfı </td><td><a href="http://www.cankaya.edu.tr/akademik_birimler/dersprog.php?derskod=A&Yacute;IT&amp;dersno=101&amp;grup=0">		</a></td><td><a href="http://engprepschool.cankaya.edu.tr/">Sayfa Linki</a></td>
		</tr>', $content);
$result = explode('</table>', $result[1]);
$content= $result[0]."</table>";
if($content == "</table>") exit(0);
$post_prod=array();
$count = 0;
while($content != "</table>" && $count <= $stop){
$line = explode("<tr>", $content);
$line = explode("</tr>", $line[1]);


$next = explode("</tr>", $content);
$content = str_replace($next[0]."</tr>", "", $content);



$columns = explode("<td>", $line[0]);
$course_code = explode("</td>", $columns[1]);
$course_code = $course_code[0];
$course_code = str_replace("&nbsp;", " ", $course_code);

$course_name = explode("</td>", $columns[2]);
$course_name = $course_name[0];

$link = explode('<a href="
	 ', $columns[3]);
$link = explode('">', $link[1]);
$link = $link[0];

$abbr = explode(" ", $course_code);

if(array_search($abbr[0], $abbreviation) &&!empty($course_name)){ 
if($count > $start){
$db_array = array();
$max_section = 0;
$last_index = 0;
$schedule = file_get_contents($link);
$schedule = explode('</thead>', $schedule);
$schedule = explode('
</table>', $schedule[1]);
$schedule = $schedule[0]."</table>";
while($schedule!="</table>"){
	$hours = explode('/',$schedule);
	$hours = explode('<', $hours[1]);

	$days = explode('<td class="text-center">', $schedule);
	for($i = 1; $i <= 7; $i++){
		$day = explode('</td>', $days[$i]);
		if($day[0] != "&nbsp;"){
			$section_list = array();
			$section = explode('- ', $days[$i]);
			for($p = 1; !empty($section[$p]); $p++){
			$section_number = explode('<br>', $section[$p]);
			$section_list[] = $section_number[0];
		}
			if($max_section < max($section_list)) $max_section = max($section_list);
			
			$db_array[$last_index]["day"]=$week_days[$i-1];
			$db_array[$last_index]["hour"]=$hours[0];
			$db_array[$last_index]["section"]=$section_list;
			$last_index++;
	}
}
$next = explode("</tr>", $schedule);
$schedule = str_replace($next[0]."</tr>", "", $schedule);
}
$department_id = array_search($abbr[0], $abbreviation) + 1;
$json ["course_code"] = $course_code;
$json ["course_name"] = $course_name;
$json ["max_section"] = $max_section;
$json ["department_id"] = $department_id;
$json ["courses"] = $db_array;
$post_prod[] = $json;
if($max_section != 0){
$query = "INSERT INTO Course(course_name, course_code, department_id, section_number) VALUES('$course_name', '$course_code', '$department_id' ,'$max_section')";
$result = mysqli_query($con, $query);
$course_id = -1;
if($result){
	$course_id = mysqli_insert_id($con);
}
$json ["course_id"] = $course_id;

foreach($db_array as $arr){
	foreach($arr["section"] as $sec){
		$week_day = $arr["day"];
		$hour = $arr["hour"];
		$query = "INSERT INTO Schedule(course_id, section, week_day, hour) VALUES('$course_id', '$sec', '$week_day', '$hour')";
		mysqli_query($con, $query);
	}
}
}
}
$count++;
}

}
echo json_encode($post_prod);

?>