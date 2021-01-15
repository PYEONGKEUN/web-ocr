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

        #selectedImgs {
            display: none;
        }

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
                            <h2>영수증 증표번호 자동인식</h2> <a href="./big"><button class="btn btn-danger" type="button">영수증 가장 큰번호 인식 바로가기</button></a>
                        </div>
                    </div>
                    <hr />
                    <div class="row">
                        <div class="col">
                            <form id="main">
                                <input multiple="multiple" id="selectedImgs" type="file" accept="image/*" onchange="onChangeSelectedImgs()">
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
                            <button type="button" class="btn btn-primary" onclick="sendImgs()">작업시작</button>
                            <button id="downExcel" type="button" class="btn btn-primary">작업결과 다운</button>
                            <button type="button" class="btn btn-danger" onclick="resetFiles()">목록 초기화</button>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col">
                            <table id="totalTable" class="table">
                                <thead class="thead-dark">
                                    <tr>
                                        <th>불러온파일명</th>
                                        <th>읽어온 번호</th>
                                        <th>파일명 변경</th>
                                        <th>삭제</th>
                                    </tr>
                                </thead>
                                <tbody id="dataTable">
                                </tbody>
                            </table>
                        </div>
                    </div>
                    <hr />
                </div>
            </div>
        </div>
    </div>
    <script>
        var isFinished = false;
        var isProgress = false;
        var hasDownoad = false;
        var remainTask = 0;
        class Queue {
            constructor() {
                this._arr = [];
            }
            enqueue(item) {
                this._arr.push(item);
            }
            dequeue() {
                return this._arr.shift();
            }
            hasData() {

                if (this._arr.length > 0) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        var finishQueue = new Queue();
        var startQueue = new Queue();
        var intervalId = null;
        var checkProcess = function() {
            console.info(remainTask);
            if (remainTask === 0) {
                clearInterval(intervalId);
                intervalId = null;
                isProgress = false;
                hasDownoad = true;
                isFinished = true;
                alert("모든 작업이 완료 되었습니다.");
            } else {
                if (finishQueue.hasData()) {
                    remainTask -= finishQueue.dequeue();
                }
            }
        }
        // 이벤트 등록
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
        /**
         *  yyyyMMdd 포맷으로 반환
         */
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

        //엑셀 다운
        $("#downExcel").click(
            function() {

                var date = new Date();
                var title = getFormatDate(date);
                fnExcelReport("totalTable", title);
            }
        );
        var FILE_LIST = new Array();

        function resetFiles() {
            if (hasDownoad) {
                alert("아직 작업 결과를 한 번도 다운로드하지 않았습니다.");
                return null;
            }
            if (isProgress) {
                alert("작업중에는 목록을 초기화 할수 없습니다.");
                return null;
            }
            FILE_LIST.splice(0, FILE_LIST.length);
            var tbody = document.getElementById("dataTable");
            while (tbody.hasChildNodes()) {
                tbody.removeChild(tbody.firstChild);
            }
            isFinished = false;
            isProgress = false;
        }


        function onChangeSelectedImgs() {
            //function() {
            ////("img in changed");
            var files = document.getElementById("selectedImgs").files;
            var fileLen = files.length;
            ////(files);
            var dataTable = document.getElementById('dataTable');

            ////(dataTable);
            //}
            // 파일을 읽고 dataTable에 정보 추가
            function readAndAdd(file) {
                // `file.name` 형태의 확장자 규칙에 주의하세요
                if (/\.(jpe?g|png|gif)$/i.test(file.name)) {
                    //(FILE_LIST);

                    var reader = new FileReader();

                    reader.addEventListener("load", function() {
                        var tmpRow = document.createElement('tr');
                        var tmpCell1 = document.createElement("td");
                        var tmpCellText1 = document.createTextNode(file.name);
                        tmpCell1.appendChild(tmpCellText1);
                        tmpRow.appendChild(tmpCell1);

                        var tmpCell2 = document.createElement("td");

                        //이미지 처리
                        var canvas = document.createElement('canvas');

                        var img = new Image();
                        var width = 2478;
                        var height = 1746;

                        canvas.width = width;
                        canvas.height = height;
                        canvas.getContext('2d').drawImage(img, 0, 0, width, height);
                        img.src = this.result;
                        img.className = "thumbnail";

                        var tmpCellText2 = document.createTextNode("");
                        //tmpCell2.appendChild(tmpCellText2);
                        tmpCell2.appendChild(img);
                        tmpRow.appendChild(tmpCell2);

                        var tmpCell3 = document.createElement("td");
                        var tmpCellText3 = document.createTextNode("");
                        tmpCell3.appendChild(tmpCellText3);
                        tmpRow.appendChild(tmpCell3);

                        var tmpCell4 = document.createElement("td");
                        var buttonDel = document.createElement("button");
                        buttonDel.innerHTML = "삭제";
                        buttonDel.type = "button";
                        buttonDel.className = "btn btn-warning";
                        buttonDel.onclick = function() {
                            var tbody = document.getElementById("dataTable");
                            tbody.removeChild(this.parentElement.parentElement);
                            //행당행의 데이터 삭제
                            FILE_LIST.splice(this.parentElement.rowIndex, 1);
                            //console.info("행삭제, 파일삭제");
                            //console.info(FILE_LIST);
                        };
                        tmpCell4.appendChild(buttonDel);
                        tmpRow.appendChild(tmpCell4);

                        dataTable.appendChild(tmpRow);
                        ////(tmpRow);
                    }, false);
                    reader.readAsDataURL(file);
                    FILE_LIST.push(file);
                }
            }

            // files 가 false가 아니라면
            if (files) {
                [].forEach.call(files, readAndAdd);
                alert("파일 " + files.length + "개가 추가되었습니다.");
            }
            document.getElementById("selectedImgs").value = null;
        }

        function sendImgs() {
            if (isFinished) {
                alert("작업이 모두 끝났습니다. 파일 목록을 초기화한 뒤 다시 시도해주세요.");
                return null;
            }
            if (isProgress) {
                alert("이미 작업중 입니다. 작업이 모두 끝나면 시도해주세요.");
                return null;
            }
            var len = FILE_LIST.length;
            if (len == 0) {
                alert("파일을 추가해 주세요");
                return null;
            }
            isProgress = true;
            remainTask = len;
            //작업전 작업해야할 목록 작성
            for (var i = 0; i < len; i++) {
                //console.info(FILE_LIST[i]);
                // html element 노드는 1 부터 시작
                startQueue.enqueue([FILE_LIST[i], i+1]);
            }
            //10ms 후 4개의 작업 동시 시작
            setTimeout(function(){asyncImgs()},10);
            setTimeout(function(){asyncImgs()},10);
            setTimeout(function(){asyncImgs()},10);
            setTimeout(function(){asyncImgs()},10);
            
            alert("작업을 시작했습니다.");
            intervalId = setInterval(checkProcess, 100);
        }

        async function asyncImgs() {
            var jobs;
            while ((jobs = startQueue.dequeue()) != undefined) {
  
                await sendFile(jobs[0], jobs[1]);
            }
        }

        //이미지 업로드 AJAX
        // 이미지 업로드가 session["mem_id"]에 종속됨 없으면 업로드 불가
        function sendFile(file, idx) {
            return new Promise((resolve, reject) => {

                console.info("sendFile : " + idx + "--" + new Date());
                var formData = new FormData();
                formData.append('mediaFile', file);

                for (var pair of formData.entries()) {
                    ////(pair[0] + ', ' + pair[1]);
                }
                $.ajax({
                    type: 'post',
                    url: './uploadimg.action',
                    data: formData,
                    success: function(status) {
                        console.info(status);

                        // 요청 결과로 td 변경
                        //읽어온 번호 열
                        console.info(status + idx);
                        var tdReadNum = getTd(idx, 2);

                        //console.info("td is" + tdReadNum);
                        if (tdReadNum.hasChildNodes) {
                            tdReadNum.removeChild(tdReadNum.firstChild);
                        }
                        var tmpCellText1 = document.createTextNode(status != "error" ? status : "읽어오지 못하였습니다.");
                        tdReadNum.appendChild(tmpCellText1);

                        // 삭제 버튼 열
                        var tdDel = getTd(idx, 4);
                        //console.info("td is" + tdReadNum);
                        if (tdDel.hasChildNodes) {
                            tdDel.removeChild(tdDel.firstChild);
                        }
                        var tmpCellTex2 = document.createTextNode(status != "error" ? "작업완료" : "오류발생");
                        tdDel.appendChild(tmpCellTex2);
                        // 파일명 변경 열
                        var tdChangedName = getTd(idx, 3);
                        var fileType;
                        var fileName;
                        if (status != "error") {
                            fileType = getTd(idx, 1).innerHTML.split(".")[1];
                            fileName = getTd(idx, 2).innerHTML + "." + fileType;
                        } else {
                            fileName = getTd(idx, 1).innerHTML;
                        }

                        var tmpCellTex3 = document.createTextNode(fileName);
                        tdChangedName.appendChild(tmpCellTex3);

                        if (status != 'error') {
                            finishQueue.enqueue(1);
                            resolve("complete");
                        } else {
                            startQueue.enqueue([file, idx]);
                            reject("not found");
                        }
                    },
                    processData: false,
                    contentType: false,
                    // 아래 error 함수를 이용해 콘솔창으로 디버깅을 한다.
                    error: function(jqXHR, textStatus, errorThrown) {
                        startQueue.enqueue([file, idx]);
                        ////(jqXHR.responseText);
                    }
                });
            });
        }

        function getTd(rowIdx, cellIdx) {
            var selectedTd;
            if (rowIdx == 1) {
                if ((selectedTd = document.querySelector("#dataTable > tr:nth-child(1) > td:nth-child(" + cellIdx + ")")) == null) {
                    selectedTd = document.querySelector("#dataTable > tr > td:nth-child(" + cellIdx + ")");
                }
            } else {
                selectedTd = document.querySelector("#dataTable > tr:nth-child(" + rowIdx + ") > td:nth-child(" + cellIdx + ")");
            }
            return selectedTd;
        }

        var WAIT_TIME = 100;

        function fnExcelReport(id, title) {
            if (!isFinished) {
                alert("작업 후에 다운로드해 주세요.");
                return null;
            }
            hasDownoad = false;

            // 엑셀 다운로드
            var tab_text = '<html xmlns:x="urn:schemas-microsoft-com:office:excel">';
            tab_text = tab_text + '<head><meta http-equiv="content-type" content="application/vnd.ms-excel; charset=UTF-8">';
            tab_text = tab_text + '<xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet>'
            tab_text = tab_text + '<x:Name>Test Sheet</x:Name>';
            tab_text = tab_text + '<x:WorksheetOptions><x:Panes></x:Panes></x:WorksheetOptions></x:ExcelWorksheet>';
            tab_text = tab_text + '</x:ExcelWorksheets></x:ExcelWorkbook></xml></head><body>';
            tab_text = tab_text + "<table border='1px'>";
            var exportTable = $('#' + id).clone();
            //열 삭제
            //console.info(exportTable);
            var thd_tr = exportTable[0].rows;
            //console.info(thd_tr);
            var thd_tr_len = thd_tr.length;
            var thd_td;
            for (var i = 0; i < thd_tr_len; i++) {

                thd_td = thd_tr[i].cells;
                console.info(thd_td);

                //thd_tr[i].deleteCell(thd_td.length - 1);
                //cells는 0부터 시작
                thd_td[thd_td.length - 1].removeChild(thd_td[thd_td.length - 1].firstChild);
                var tmpStr = "";
                if (i == 0) {
                    tmpStr = "날짜";
                } else {
                    // 파일 명에서 날짜 분리 SCAN_20200302_173106407 -> 20200302
                    tmpStr = thd_td[0].innerHTML.split("_")[1];
                    tmpStr = tmpStr.substring(0,4) +"-"+tmpStr.substring(4,6)+"-"+tmpStr.substring(6);
                }
                console.info(tmpStr);
                var tmpText = document.createTextNode(tmpStr);
                thd_td[thd_td.length - 1].appendChild(tmpText);
            }
            exportTable.find('input').each(function(index, elem) {
                $(elem).remove();
            });
            tab_text = tab_text + exportTable.html();
            tab_text = tab_text + '</table></body></html>';
            var data_type = 'data:application/vnd.ms-excel';
            var ua = window.navigator.userAgent;
            var msie = ua.indexOf("MSIE ");
            var fileName = title + '.xls';
            //Explorer 환경에서 다운로드
            if (msie > 0 || !!navigator.userAgent.match(/Trident.*rv\:11\./)) {
                if (window.navigator.msSaveBlob) {
                    var blob = new Blob([tab_text], {
                        type: "application/csv;charset=utf-8;"
                    });
                    navigator.msSaveBlob(blob, fileName);
                }
            } else {
                var blob2 = new Blob([tab_text], {
                    type: "application/csv;charset=utf-8;"
                });
                var filename = fileName;
                var elem = window.document.createElement('a');
                elem.href = window.URL.createObjectURL(blob2);
                elem.download = filename;
                document.body.appendChild(elem);
                elem.click();
                document.body.removeChild(elem);
            }
            // 이미지 다운로드
            delayDownloadImg();
        }

        function downloagImg(i) {
            return new Promise(function(resolve, reject) {
                setTimeout(() => {
                    // html element 노드는 1 부터 시작
                    var imgName = getTd(i + 1, 3).innerHTML;
                    var elem = window.document.createElement('a');
                    elem.href = window.URL.createObjectURL(FILE_LIST[i]);
                    elem.download = imgName;
                    document.body.appendChild(elem);
                    elem.click();
                    document.body.removeChild(elem);
                    resolve("complete");
                }, WAIT_TIME);
            });
        }

        async function delayDownloadImg() {
            var len = FILE_LIST.length;
            for (var i = 0; i < len; i++) {
                //console.info(FILE_LIST[i]);
                await downloagImg(i);
            }
        }
    </script>
</body>

</html>
