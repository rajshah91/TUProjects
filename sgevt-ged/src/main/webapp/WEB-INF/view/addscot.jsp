<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>
<head>
<title>Add Scot</title>

<!-- <script src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.2/jquery.min.js"></script>
<script src="http://code.jquery.com/ui/1.9.2/jquery-ui.min.js"></script> -->

<link href="https://code.jquery.com/ui/1.10.4/themes/ui-lightness/jquery-ui.css" rel="stylesheet">
<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>

<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>




<script type="text/javascript">
	$(document).ready(
			function() {
				$("#approbation_date").datepicker({ dateFormat: 'yy-mm-dd' });
				$("#collecting_date").datepicker({ dateFormat: 'yy-mm-dd' });

				window.filesArr = new Array();

				$("#addfilebtn").click(function() {

					var file = $('#files')[0].files[0];
					var type = $('#type').val();
					var url = $('#url').val();

					if (file && type != "") {
						var data = {};
						data.file = file;
						data.type = type;
						data.url = url;
						window.filesArr.push(data);

						$('#files').val(null);
						$('#type').val(null);
						$('input[name=url').val('');

						$("#modalclosebtn").trigger("click");

						$("#displaytextdiv").html(preparedTextForDisplay());

					}
				});

				$("#submitbtn").click(function(e) {
					e.preventDefault();
					$("div#divLoading").addClass('show');
					var mydata = {};
					
					mydata.sirenCP= $("#sirencp").val();
					mydata.status= $("#status").val();
					mydata.approbationDate= $('#approbation_date').datepicker({ dateFormat: 'yyyy-MM-dd' }).val();
					mydata.collectingDate= $('#collecting_date').datepicker({ dateFormat: 'yyyy-MM-dd' }).val();
					mydata.comment= $("#comment").val();
					mydata.filesMetadata= getFilesMetadataJson();
					/* mydata.file = window.filesArr[0].file; */
					$.ajax({
						type : "POST",
						/* enctype : 'multipart/form-data', */
						/* processData : false, */
						/* contentType : false, */
						url : "/scot/savescot",
						data : JSON.stringify(mydata),
						dataType: 'text/json',
			            contentType: "application/json; charset=utf-8",
			            complete: function(xhr) {
			                if (xhr.readyState == 4) {
			                    if (xhr.status == 201) {
			                        alert("Created");
			                        uploadScotFiles();
			                    }else if(xhr.status == 200 && xhr.responseText == "Error Occured"){
			                    	alert(xhr.responseText);
			                    	$("div#divLoading").removeClass('show');
				                }
			                }
			            }
					});

				});


				function uploadScotFiles(){
					var fd = new FormData(); 
	                var files = new Array();
	                for (var i = 0; i < window.filesArr.length; i++) {
						fd.append('file', window.filesArr[i].file); 
					}
					$.ajax({ 
	                    url: '/scot/addscot/uploadfiles/'+$("#sirencp").val(), 
	                    type: 'post', 
	                    data: fd, 
	                    contentType: false, 
	                    processData: false, 
	                    success: function(response){ 
	                        if(response == "success"){ 
	                           alert('file uploaded'); 
	                        }else if(response == "fail"){ 
	                            alert('file not uploaded'); 
	                        } 
	                    }, 
	                }); 

	                $("div#divLoading").removeClass('show'); 
					clearFormAndVariables();
				}

				function getFilesMetadataJson() {
					var jsonArray=new Array();
					for (var i = 0; i < window.filesArr.length; i++) {
						var mydata={};
						mydata.originalURL = window.filesArr[i].url;
						mydata.type=window.filesArr[i].type;
						mydata.fileName=window.filesArr[i].file.name;
						jsonArray.push(mydata);
					}
					return jsonArray;
				}

				function preparedTextForDisplay() {
					var txt = "";
					for (var i = 0; i < window.filesArr.length; i++) {
						txt += window.filesArr[i].file.name + " : "
								+ window.filesArr[i].type + " : "
								+ window.filesArr[i].url + " <br/> "
					}
					return txt;
				}

				function clearFormAndVariables(){
					$('#scotform')[0].reset();
					window.filesArr = [] ;
					$("#displaytextdiv").html("");
				}

			});
</script>



<style>
* {
	box-sizing: border-box;
}

input[type=text], select, textarea {
	width: 100%;
	padding: 12px;
	border: 1px solid #ccc;
	border-radius: 4px;
	resize: vertical;
}

label {
	padding: 12px 12px 12px 0;
	display: inline-block;
}

input[type=submit] {
	background-color: #4CAF50;
	color: white;
	padding: 12px 20px;
	border: none;
	border-radius: 4px;
	cursor: pointer;
	float: right;
}

input[type=submit]:hover {
	background-color: #45a049;
}

#divLoading
{
    display : none;
}
#divLoading.show
{
    display : block;
    position : fixed;
    z-index: 100;
    background-image : url('http://loadinggif.com/images/image-selection/3.gif');
    background-color:#666;
    opacity : 0.4;
    background-repeat : no-repeat;
    background-position : center;
    left : 0;
    bottom : 0;
    right : 0;
    top : 0;
}
#loadinggif.show
{
    left : 50%;
    top : 50%;
    position : absolute;
    z-index : 101;
    width : 32px;
    height : 32px;
    margin-left : -16px;
    margin-top : -16px;
}

.container {
	border-radius: 5px;
	background-color: #f2f2f2;
	padding: 20px;
	width: 60%
}

.modalcontainer {
	border-radius: 5px;
	background-color: #f5f5f5;
	padding: 20px;
	width: 100%
}

.col-25 {
	float: left;
	width: 25%;
	margin-top: 6px;
}

.col-75 {
	float: left;
	width: 75%;
	margin-top: 6px;
}

/* Clear floats after the columns */
.row:after {
	content: "";
	display: table;
	clear: both;
}

/* Responsive layout - when the screen is less than 600px wide, make the two columns stack on top of each other instead of next to each other */
@media screen and (max-width: 600px) {
	.col-25, .col-75, input[type=submit] {
		width: 100%;
		margin-top: 0;
	}
}
</style>
</head>


<body>

	<h1>
		<strong>New Scot</strong>
	</h1>


	<div class="container">
		<form:form action="" method="post" style="" enctype="multipart/form-data" id="scotform">

			<div class="row">
				<div class="col-25">
					<label for="">SIREN CP</label>
				</div>
				<div class="col-75">
					<input type="text" id="sirencp" name="sirencp"
						placeholder="SIREN CP..">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="">Status</label>
				</div>
				<div class="col-75">
					<select id="status" name="status">
						<option value="approved">Approved</option>
						<option value="pending">Pending</option>
						<option value="validated">Validated</option>
					</select>
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="country">Approbation Date</label>
				</div>
				<div class="col-75">
					<input type="text" id="approbation_date" name="approbation_date"
						placeholder="Select Approbation Date..">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="country">Collecting Date</label>
				</div>
				<div class="col-75">
					<input type="text" id="collecting_date" name="collecting_date"
						placeholder="Select Collecting Date..">
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for="subject">Comment</label>
				</div>
				<div class="col-75">
					<textarea id="comment" name="comment"
						placeholder="Write something.." style="height: 200px" rows="4"></textarea>
				</div>
			</div>

			<div class="row">
				<div class="col-25">
					<label for=""></label>
				</div>
				<div class="col-75">
					<input type="button" id="openmodalbtn" name="openmodalbtn"
						placeholder="Select File.." value="Add File"
						class="btn btn-info btn-lg" data-toggle="modal"
						data-target="#myModal">
				</div>
			</div>


			<div class="row">
				<div class="col-25" id="">
					<!-- <input type="text" id="urls" name="urls[]" > 
					<input type="text" id="types" name="types[]" > 
					<input type="text" id="uploadfiles" name="uploadfiles[]" > -->
				</div>
				<div class="col-75" id="displaytextdiv"></div>
			</div>



			<div class="">
				<input type="submit" id="submitbtn" value="Save" style="float: left">
			</div>

		</form:form>
	</div>










	<div class="modal fade" id="myModal" role="dialog" style="">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">

				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">&times;</button>
					<h4 class="modal-title">Add File</h4>
				</div>

				<div class="modal-body">

					<div class="modalcontainer">

						<div class="row">
							<div class="col-25">
								<label for="">Type</label>
							</div>
							<div class="col-75">
								<select id="type" name="type">
									<option value="repport">Repport</option>
									<option value="padd">Padd</option>
									<option value="doo">Doo</option>
								</select>
							</div>
						</div>

						<div class="row">
							<div class="col-25">
								<label for="">URL</label>
							</div>
							<div class="col-75">
								<input type="text" id="url" name="url"
									placeholder="Original URL..">
							</div>
						</div>

						<div class="row">
							<div class="col-25">
								<label for="">FILE</label>
							</div>
							<div class="col-75">
								<input type="file" id="files" name="files"
									placeholder="Select File..">
							</div>
						</div>


						<div class="row">
							<div class="col-25">
								<label for=""></label>
							</div>
							<div class="col-75">
								<input type="button" id="addfilebtn" name="addfilebtn"
									value="Add" class="btn btn-info btn-lg">
							</div>
						</div>


					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn" id="modalclosebtn"
						data-dismiss="modal">Close</button>
				</div>
			</div>

		</div>
	</div>

<div id="divLoading"></div>

</body>
</html>