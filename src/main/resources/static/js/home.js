$(function () {
    $("#delete_remark").click(function () {
        $("#remark").val("");
        $("#select_file").val("")
    });
    $("#file_submit").click(function () {
        if ($("#select_file").val().trim() == "") {
            alert("未选择文件");
            return false
        }
        var remarkText = $("#remark").val().trim();
        if (remarkText.length > 20) {
            remarkText = remarkText.substr(0, 20);
            $("#remark").val(remarkText)
        }
        var useCapacity = parseInt($("#useCapacity").text());
        var maxCapacity = parseInt($("#maxCapacity").text());
        if (useCapacity >= maxCapacity) {
            alert("您的存储空间已满，清理空间后继续");
            $("#remark").val("");
            $("#select_file").val("");
            return false
        }
    });
    $("#createFolder").click(function () {
        var foldername = $("#folderName").val().trim();
        if (foldername.length <= 0) {
            alert("文件夹名不可为空");
            return false
        }
    });
    $("#fuzzySelect").blur(function () {
        var matchCode = $("#fuzzySelect").val().trim();
        if (matchCode.length <= 0) {
            alert("检索关键字为空");
            return false
        }
        $("#fuzzySelect_button").trigger("click")
    });
    $(".fileCheckBox").click(function () {
        setBackGroundColor()
    });
    $(".folderCheckBox").click(function () {
        setBackGroundColor()
    });
    var selectFlag = true;

    function setBackGroundColor() {
        var isSelectAll = true;
        $(".fileCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                $(".fileItem").eq(index).css("background", "rgb(223, 224, 225)")
            } else {
                isSelectAll = false;
                selectFlag = true;
                $(".fileItem").eq(index).css("background", "")
            }
        });
        $(".folderCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                $(".folderItem").eq(index).css("background", "rgb(223, 224, 225)")
            } else {
                isSelectAll = false;
                selectFlag = true;
                $(".folderItem").eq(index).css("background", "")
            }
        });
        if (!isSelectAll) {
            $("#selectAll").text("全选")
        } else {
            $("#selectAll").text("取消")
        }
    }

    $("#selectAll").click(function () {
        if (selectFlag) {
            $(".fileCheckBox").each(function (index, element) {
                $(element).prop("checked", true)
            });
            $(".folderCheckBox").each(function (index, element) {
                $(element).prop("checked", true)
            });
            $(".fileItem").each(function (index, element) {
                $(element).css("background", "rgb(223, 224, 225)")
            });
            $(".folderItem").each(function (index, element) {
                $(element).css("background", "rgb(223, 224, 225)")
            })
        } else {
            $(".fileCheckBox").each(function (index, element) {
                $(element).prop("checked", false)
            });
            $(".folderCheckBox").each(function (index, element) {
                $(element).prop("checked", false)
            });
            $(".fileItem").each(function (index, element) {
                $(element).css("background", "")
            });
            $(".folderItem").each(function (index, element) {
                $(element).css("background", "")
            })
        }
        selectFlag = !selectFlag;
        if (selectFlag) {
            $("#selectAll").text("全选")
        } else {
            $("#selectAll").text("取消")
        }
    });
    $("#deleteSelectAll").click(function () {
        var requestFlag = false;
        $(".fileCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                requestFlag = true
            }
        });
        $(".folderCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                requestFlag = true
            }
        });
        if (!requestFlag) {
            alert("无选中项");
            return
        }
        if (!confirm("是否删除选中项?")) {
            return
        }
        var uri = "file/browse/delete";
        var form = $("<form></form>");
        form.attr("action", uri);
        form.attr("method", "post");
        $(".fileCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                requestFlag = true;
                var targetElement = $(element).clone();
                form.append(targetElement)
            }
        });
        $(".folderCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                requestFlag = true;
                var targetElement = $(element).clone();
                form.append(targetElement)
            }
        });
        if (requestFlag) {
            form.appendTo("body");
            form.hide();
            form.submit()
        }
        $(".fileItem").each(function (index, element) {
            $(element).css("background", "")
        });
        $(".folderItem").each(function (index, element) {
            $(element).css("background", "")
        })
    });
    $("#downloadSelectAll").click(function () {
        var requestFlag = false;
        $(".fileCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                requestFlag = true
            }
        });
        if (!requestFlag) {
            alert("无选中项");
            return
        }
        if (!confirm("是否下载选中项?")) {
            return
        }
        $(".fileCheckBox").each(function (index, element) {
            if ($(element).prop("checked")) {
                var url = $(".fileCheckBoxURL").eq(index).val();
                try {
                    var elemIF = document.createElement("iframe");
                    elemIF.src = url;
                    elemIF.style.display = "none";
                    document.body.appendChild(elemIF)
                } catch (e) {
                    alert("下载异常！")
                }
            }
        });
        $(".fileCheckBox").each(function (index, element) {
            $(element).prop("checked", false)
        });
        $(".folderCheckBox").each(function (index, element) {
            $(element).prop("checked", false)
        });
        $(".fileItem").each(function (index, element) {
            $(element).css("background", "")
        });
        $(".folderItem").each(function (index, element) {
            $(element).css("background", "")
        });
        $("#selectAll").text("全选");
        selectFlag = true
    });
    $(".share_A").click(fun01);

    function fun01() {
        var fileid = $(this).attr("id");
        $.ajax({
            async: true,
            data: {fileid: fileid},
            dataType: "text",
            url: "file/browse/share",
            success: function (data) {
                imgShow("#outerdiv", "#innerdiv", "#bigimg", data)
            },
            error: function () {
                alert("分享失败")
            },
            type: "post"
        })
    }

    function imgShow(outerdiv, innerdiv, bigimg, src) {
        $(bigimg).attr("src", src);
        $(bigimg).attr("src", src).load(function () {
            var windowW = $(window).width();
            var windowH = $(window).height();
            var realWidth = this.width;
            var realHeight = this.height;
            var imgWidth, imgHeight;
            var scale = 0.8;
            if (realHeight > windowH * scale) {
                imgHeight = windowH * scale;
                imgWidth = imgHeight / realHeight * realWidth;
                if (imgWidth > windowW * scale) {
                    imgWidth = windowW * scale
                }
            } else {
                if (realWidth > windowW * scale) {
                    imgWidth = windowW * scale;
                    imgHeight = imgWidth / realWidth * realHeight
                } else {
                    imgWidth = realWidth;
                    imgHeight = realHeight
                }
            }
            $(bigimg).css("width", imgWidth);
            var w = (windowW - imgWidth) / 2;
            var h = (windowH - imgHeight) / 2;
            $(innerdiv).css({"top": h, "left": w});
            $(outerdiv).fadeIn("fast")
        });
        $(outerdiv).click(function () {
            $(this).fadeOut("fast")
        })
    }
});