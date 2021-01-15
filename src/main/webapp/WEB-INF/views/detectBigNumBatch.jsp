<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <title>web-ocr</title>
    <!--bootstrap-->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta/js/bootstrap.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery.fileDownload/1.4.2/jquery.fileDownload.min.js" integrity="sha512-MZrUNR8jvUREbH8PRcouh1ssNRIVHYQ+HMx0HyrZTezmoGwkuWi1XoaRxWizWO8m0n/7FXY2SSAsr2qJXebUcA==" crossorigin="anonymous"></script>
    <style>
        html {
            height: 100%;
            overflow: hidden;
        }
        body {
            margin: 0;
            padding: 0;
            perspective: 1px;
            transform-style: preserve-3d;
            height: 100%;
            overflow-x: hidden;
            overflow-y: auto;
            font-family: "Luna";
            background: #454c55;
        }
        .content {

            background: white;
            padding: 30px;
            border-radius: 5px;
        }
        .hide {
            display: none;
        }
        .thumbnail {
            width: 180px;
            height: auto;
        }
        /*#selectedImgs {*/
        /*    display: none;*/
        /*}*/
        #myBar {
            width: 0%;
            height: 40px;
            background-color: #277daa;
            text-align: center;
            /* To center it horizontally (if you want) */
            line-height: 30px;
            /* To center it vertically */
            color: white;
        }
    </style>
    <script>
        var agent = navigator.userAgent.toLowerCase();
        console.info(agent);
        console.info(navigator.appName);
        console.info(agent.indexOf('trident'));
        console.info(agent.indexOf("msie"));
        if ((navigator.appName == 'Netscape' && agent.indexOf('trident') != -1) || (agent.indexOf("msie") != -1)) {
            alert("해당 페이지는 익스플로러에서 작동하지 않습니다. 다른 브라우저로 접속해 주십시오.");
            window.location.href = './links';
        }

    </script>
</head>

<body>
    <div class="container" style="margin-top: 100px;">
        <div class="row">
            <div class="col-lg">
                <div class="content">
                    <div class="row">
                        <div class="col">
                            <h2>영수증 가장 큰 번호 자동인식</h2><a href="./"><button class="btn btn-primary" type="button">영수증 증표번호 자동인식</button></a>
                        </div>
                    </div>
                    <hr />
                    <div class="row">
                        <div class="col">
                            <form id="main">
                                <input multiple="multiple" id="selectedImgs" type="file" accept="image/*">
                            </form>
                            <button id="btnSelectedImgs" class="btn btn-primary" type="button">파일추가</button>
                        </div>
                    </div>
                    <hr />
                    <div class="row">
                        <div class="col">
                            <h2>선택된 이미지</h2>
                        </div>
                        <div class="col">
                            <button type="button" class="btn btn-primary" onclick="getBigNumberBatch()">작업시작</button>
                            <button id="downExcel" type="button" class="btn btn-primary">작업결과 다운</button>
                            <button type="button" class="btn btn-danger" onclick="resetFiles()">목록 초기화</button>
                        </div>
                    </div>
                    <hr />
                </div>
            </div>
        </div>
    </div>
    <script>

        // ---- onload
        $(document).ready(function() {
            // 파일추가 버튼 이벤트
            $('#btnSelectedImgs').click(function(e) {

                if (isFinished) {
                    alert("작업이 모두 끝났습니다. 파일 목록을 초기화한 뒤 다시 시도해주세요.");
                    return null;
                }
                if (isProgress) {
                    alert("이미 작업중 입니다. 작업이 모두 끝나면 시도해주세요.");
                    return null;
                }
                if (hasDownoad) {
                    alert("아직 작업 결과를 한 번도 다운로드하지 않았습니다.");
                    return null;
                }
                $('#selectedImgs').click();
            });
        });

        //----- 서비스


        // state
        var isFinished = false;
        var isProgress = false;
        var hasDownoad = false;

        // functions
        function getFormatDate(date) {
            var year = date.getFullYear(); //yyyy
            var month = (1 + date.getMonth()); //M
            month = month >= 10 ? month : '0' + month; //month 두자리로 저장
            var day = date.getDate(); //d
            day = day >= 10 ? day : '0' + day; //day 두자리로 저장
            var hour = date.getHours();
            hour = hour >= 10 ? hour : '0' + hour; //hour 두자리로 저장
            var min = date.getMinutes();
            min = min >= 10 ? min : '0' + min; //min 두자리로 저장
            var sec = date.getSeconds();
            sec = sec >= 10 ? sec : '0' + sec; //sec 두자리로 저장
            return year + '_' + month + '_' + day + "_" + hour + "" + min + "" + sec;
        }

        function getBigNumberBatch(){
            console.log('----- getBigNumberBatch()  start');
            var formData = new FormData();
            var files = document.getElementById("selectedImgs").files;
            var fileLen = files.length;

            console.log(files);
            for(var i = 0; i < fileLen; i++){
                formData.append('file', files[i]);
            }
            // FormData의 key 확인
            // for (let key of formData.keys()) {
            //     console.log(key);
            // }
            //
            // // FormData의 value 확인
            // for (let value of formData.values()) {
            //     console.log(value);
            // }

            $.ajax({
                type: "POST",
                url: "./bigbatch",
                data: formData,
                processData: false,
                contentType: false,
                success : function(data) {
                    console.log(data);
                    if(data){
                        alert("Success");
                    }else{
                        alert(data);
                    }
                },
                err : function(err) {
                    alert(err.status);
                }

            });
        }


    </script>
</body>

</html>
