const emsapi = function () {
    'use strict';
    const api = new Object();
    let testMode = false;

    const SERVER_URL = "http://localhost:8080";
    const JSON_FORMAT = "json";

    api.getTemperature = async function (startDt, endDt, unit) {
        startDt = startDt == undefined ? "" : startDt.trim();
        endDt = endDt == undefined ? "" : endDt.trim();
        unit = unit == undefined ? "" : unit.trim();

        if (testMode) {
            if (startDt == "" && endDt == "" && unit == "") {
                return getTestTemperatureByRealTime();
            }
        }

        let url = SERVER_URL + "/temperature";

        if (startDt != "") {
            url += "&startDt=" + startDt;
        }

        if (endDt != "") {
            url += "&endDt=" + endDt;
        }

        if (unit != "") {
            url += "&unit=" + unit
        }

        console.log("url:" + url);
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("error:" + url);
        }
        return await response.json();
    }

    api.getHumidity = async function (startDt, endDt, unit) {
        startDt = startDt == undefined ? "" : startDt.trim();
        endDt = endDt == undefined ? "" : endDt.trim();
        unit = unit == undefined ? "" : unit.trim();

        if (testMode) {
            if (startDt == "" && endDt == "" && unit == "") {
                return getTestHumiditybyRealTime();
            }
        }

        let url = SERVER_URL + "/humidity";

        if (startDt != "") {
            url += "&startDt=" + startDt;
        }

        if (endDt != "") {
            url += "&endDt=" + endDt;
        }

        if (unit != "") {
            url += "&unit=" + unit
        }

        console.log("url:" + url);
        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("error:" + url);
        }
        return await response.json();
    }

    function getTestTemperatureByRealTime() {
        const result = new Object();
        result.dateTime = getTimestamp();
        result.temperature = getRandomInt(20, 50);
        return result;
    };

    function getTestHumiditybyRealTime() {
        const result = new Object();
        result.dateTime = getTimestamp();
        result.humidity = getRandomInt(50, 100);
        return result;
    }

    //javascript 정수 난수 생성하기
    //https://developer.mozilla.org/ko/docs/Web/JavaScript/Reference/Global_Objects/Math/random
    function getRandomInt(min, max) {
        min = Math.ceil(min);
        max = Math.floor(max);
        return Math.floor(Math.random() * (max - min)) + min; //최댓값은 제외, 최솟값은 포함
    }

    function getTimestamp() {
        var today = new Date();
        today.setHours(today.getHours() + 9);
        return today.toISOString().replace('T', ' ').substring(0, 19);
    }

    return api;
}