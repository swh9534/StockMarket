import { showSpinner, hideSpinner, notifyError } from "@/scripts/store-popups";

export const Response = {
  SUCCESS: 0,
  FAIL: 1,
};

const AUTH_ERRORS = [9001, 9004];

const checkSession = (code: number, message: string, notify: boolean) => {
  const displayMessage = message || "세션 확인 중 오류가 발생했습니다.";
  if (notify) {
    notifyError(displayMessage);
  }
  if (AUTH_ERRORS.includes(code)) {
    setTimeout(() => {
      window.location.href = "/logout";
    }, 2000);
  }
};

const apiHeaders = {
  "Content-Type": "application/json",
};

const handleResponse = async (response: Response, notify: boolean) => {
  if (response.ok) {
    const data = await response.json();
    if (data.result === Response.SUCCESS) {
      return data;
    } else {
      checkSession(data.code, data.message, notify);
      return data;
    }
  } else {
    const errorText =
      (await response.text()) || "서버에서 응답 본문을 제공하지 않았습니다.";
    const errorMsg = `HTTP ${response.status}: ${response.statusText} - ${errorText}`;
    console.error("❌ handleResponse 실패:", errorMsg);
    if (notify) {
      notifyError(errorMsg || "알 수 없는 서버 오류가 발생했습니다.");
    }
    return {
      result: Response.FAIL,
      code: Response.FAIL,
      message: errorMsg,
      body: null,
    };
  }
};

const handleError = (error: any, notify: boolean) => {
  const errorMsg =
    error.message || error || "알 수 없는 네트워크 오류가 발생했습니다.";
  console.error("❌ handleError 발생:", errorMsg);
  if (notify) {
    notifyError(errorMsg);
  }
  return { result: Response.FAIL, code: 2, message: errorMsg, body: null };
};

const sendRequest = async (
  method: string,
  url: string,
  headers: { [key: string]: any } | null = {},
  body: any = null,
  notify: boolean
) => {
  showSpinner();
  try {
    const options: RequestInit = {
      method,
      headers: { ...apiHeaders, ...headers },
    };

    if (method !== "GET" && method !== "HEAD") {
      options.body = JSON.stringify(body);
    }

    const response = await fetch(url, options);
    return await handleResponse(response, notify);
  } catch (error: any) {
    return handleError(error, notify);
  } finally {
    hideSpinner();
  }
};

const buildUrlWithParams = (
  url: string,
  params: { [key: string]: any } | null
) => {
  if (!params) return url;
  const searchParams = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      value.forEach((val) => searchParams.append(key, val));
    } else {
      searchParams.append(key, value);
    }
  });
  return `${url}?${searchParams.toString()}`;
};

const apiCall = {
  Response,

  get: async (
    url: string,
    headers: { [key: string]: any } | null,
    queryParams: { [key: string]: any } | null
  ) => {
    const fullUrl = buildUrlWithParams(url, queryParams);
    console.log("GET 요청:", fullUrl); // 디버깅용 로그 추가
    return await sendRequest("GET", fullUrl, headers, null, true);
  },

  justGet: async (
    url: string,
    headers: { [key: string]: any } | null,
    queryParams: { [key: string]: any } | null
  ) => {
    const fullUrl = buildUrlWithParams(url, queryParams);
    console.log("justGet 요청:", fullUrl); // 디버깅용 로그 추가
    return await sendRequest("GET", fullUrl, headers, null, false);
  },

  post: async (
    url: string,
    headers: { [key: string]: any } | null,
    requestBody: any
  ) => {
    return await sendRequest("POST", url, headers, requestBody, true);
  },

  justPost: async (
    url: string,
    headers: { [key: string]: any } | null,
    requestBody: any
  ) => {
    return await sendRequest("POST", url, headers, requestBody, false);
  },

  put: async (
    url: string,
    headers: { [key: string]: any } | null,
    requestBody: any
  ) => {
    return await sendRequest("PUT", url, headers, requestBody, true);
  },

  justPut: async (
    url: string,
    headers: { [key: string]: any } | null,
    requestBody: any
  ) => {
    return await sendRequest("PUT", url, headers, requestBody, false);
  },

  delete: async (
    url: string,
    headers: { [key: string]: any } | null,
    requestBody: any
  ) => {
    return await sendRequest("DELETE", url, headers, requestBody, true);
  },

  justDelete: async (
    url: string,
    headers: { [key: string]: any } | null,
    requestBody: any
  ) => {
    return await sendRequest("DELETE", url, headers, requestBody, false);
  },

  download: async (
    url: string,
    headers: { [key: string]: any } | null,
    queryParams: { [key: string]: any } | null,
    filename: string
  ) => {
    showSpinner();
    try {
      const fullUrl = buildUrlWithParams(url, queryParams);
      const response = await fetch(fullUrl, {
        headers: { ...apiHeaders, ...headers },
      });
      if (response.ok) {
        const blob = await response.blob();
        const objectUrl = window.URL.createObjectURL(blob);
        const a = document.createElement("a");
        a.style.display = "none";
        a.href = objectUrl;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(objectUrl);
        return {
          result: Response.SUCCESS,
          code: Response.SUCCESS,
          message: "",
          body: null,
        };
      } else {
        return await handleResponse(response, true);
      }
    } catch (error: any) {
      return handleError(error, true);
    } finally {
      hideSpinner();
    }
  },

  upload: async (
    url: string,
    headers: { [key: string]: any } | null,
    file: File
  ) => {
    showSpinner();
    try {
      const formData = new FormData();
      formData.append("file", file);
      const response = await fetch(url, {
        method: "POST",
        body: formData,
        headers: { ...headers },
      });
      return await handleResponse(response, true);
    } catch (error: any) {
      return handleError(error, true);
    } finally {
      hideSpinner();
    }
  },
};

export default apiCall;
