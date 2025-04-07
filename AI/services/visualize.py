# services/visualize.py
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import json
import os
import io
import base64
from typing import List, Dict, Any, Optional
from datetime import datetime

def plot_predictions(test,predicted):
    plt.plot(test, color='red',label='Real Stock Price')
    plt.plot(predicted, color='blue',label='Predicted Stock Price')
    plt.title('Stock Price Prediction')
    plt.xlabel('Time')
    plt.ylabel('Stock Price')
    plt.legend()
    plt.show()

def generate_training_history_plot(job_id: str) -> str:
    """
    학습 이력 그래프 생성
    
    Args:
        job_id: 학습 작업 ID
        
    Returns:
        base64 인코딩된 그래프 이미지
    """
    try:
        # 학습 로그 파일 읽기
        log_file = f"data/training_logs/{job_id}.json"
        
        if not os.path.exists(log_file):
            return None
            
        with open(log_file, "r") as f:
            log_data = json.load(f)
            
        # 학습 이력 데이터 추출
        epochs = []
        train_losses = []
        val_losses = []
        
        # 학습 진행 중인 로그에서 정보 추출
        if "epoch_history" in log_data:
            for epoch, data in enumerate(log_data["epoch_history"]):
                epochs.append(epoch + 1)
                train_losses.append(data["train_loss"])
                val_losses.append(data["val_loss"])
        else:
            # 직접 학습 로그 파일에서 추출해야 하는 경우
            return None
            
        # 그래프 생성
        plt.figure(figsize=(10, 6))
        plt.plot(epochs, train_losses, 'b-', label='Training Loss')
        plt.plot(epochs, val_losses, 'r-', label='Validation Loss')
        plt.title('Training and Validation Loss')
        plt.xlabel('Epochs')
        plt.ylabel('Loss')
        plt.legend()
        plt.grid(True)
        
        # 그래프를 메모리에 저장
        buffer = io.BytesIO()
        plt.savefig(buffer, format='png')
        buffer.seek(0)
        
        # Base64 인코딩
        image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8')
        plt.close()
        
        return image_base64
        
    except Exception as e:
        print(f"학습 이력 그래프 생성 중 오류 발생: {str(e)}")
        return None

def generate_confusion_matrix(y_true: List[int], y_pred: List[int], labels: List[str] = None) -> str:
    """
    혼동 행렬 생성
    
    Args:
        y_true: 실제 레이블
        y_pred: 예측 레이블
        labels: 클래스 레이블 목록
        
    Returns:
        base64 인코딩된 혼동 행렬 이미지
    """
    try:
        from sklearn.metrics import confusion_matrix
        
        # 혼동 행렬 계산
        cm = confusion_matrix(y_true, y_pred)
        
        # 그래프 생성
        plt.figure(figsize=(8, 6))
        plt.imshow(cm, interpolation='nearest', cmap=plt.cm.Blues)
        plt.title('Confusion Matrix')
        plt.colorbar()
        
        # 클래스 레이블 설정
        if labels:
            tick_marks = np.arange(len(labels))
            plt.xticks(tick_marks, labels, rotation=45)
            plt.yticks(tick_marks, labels)
        
        # 값 표시
        thresh = cm.max() / 2.
        for i in range(cm.shape[0]):
            for j in range(cm.shape[1]):
                plt.text(j, i, cm[i, j],
                        horizontalalignment="center",
                        color="white" if cm[i, j] > thresh else "black")
        
        plt.tight_layout()
        plt.ylabel('True label')
        plt.xlabel('Predicted label')
        
        # 그래프를 메모리에 저장
        buffer = io.BytesIO()
        plt.savefig(buffer, format='png')
        buffer.seek(0)
        
        # Base64 인코딩
        image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8')
        plt.close()
        
        return image_base64
        
    except Exception as e:
        print(f"혼동 행렬 생성 중 오류 발생: {str(e)}")
        return None

def generate_performance_metrics_plot(metrics: Dict[str, float]) -> str:
    """
    성능 지표 그래프 생성
    
    Args:
        metrics: 성능 지표 데이터
        
    Returns:
        base64 인코딩된 그래프 이미지
    """
    try:
        # 사용할 지표 필터링
        plot_metrics = {k: v for k, v in metrics.items() if isinstance(v, (int, float)) and k not in ['training_time']}
        
        # 그래프 생성
        plt.figure(figsize=(8, 6))
        
        # 바 그래프
        bars = plt.bar(plot_metrics.keys(), plot_metrics.values(), color='skyblue')
        
        # 바 위에 값 표시
        for bar in bars:
            height = bar.get_height()
            plt.text(bar.get_x() + bar.get_width()/2., height,
                    f'{height:.3f}',
                    ha='center', va='bottom')
        
        plt.title('Model Performance Metrics')
        plt.ylabel('Score')
        plt.ylim(0, 1.0)
        plt.tight_layout()
        
        # 그래프를 메모리에 저장
        buffer = io.BytesIO()
        plt.savefig(buffer, format='png')
        buffer.seek(0)
        
        # Base64 인코딩
        image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8')
        plt.close()
        
        return image_base64
        
    except Exception as e:
        print(f"성능 지표 그래프 생성 중 오류 발생: {str(e)}")
        return None

def generate_prediction_distribution(predictions: List[float], bins: int = 10) -> str:
    """
    예측값 분포 히스토그램 생성
    
    Args:
        predictions: 모델 예측값 목록
        bins: 히스토그램 구간 수
        
    Returns:
        base64 인코딩된 히스토그램 이미지
    """
    try:
        plt.figure(figsize=(8, 6))
        plt.hist(predictions, bins=bins, color='skyblue', edgecolor='black')
        plt.title('Prediction Distribution')
        plt.xlabel('Prediction Value')
        plt.ylabel('Frequency')
        plt.grid(True, alpha=0.3)
        
        # 그래프를 메모리에 저장
        buffer = io.BytesIO()
        plt.savefig(buffer, format='png')
        buffer.seek(0)
        
        # Base64 인코딩
        image_base64 = base64.b64encode(buffer.getvalue()).decode('utf-8')
        plt.close()
        
        return image_base64
        
    except Exception as e:
        print(f"예측값 분포 히스토그램 생성 중 오류 발생: {str(e)}")
        return None

def export_model_report(job_id: str, output_format: str = 'markdown') -> str:
    """
    모델 리포트 생성
    
    Args:
        job_id: 학습 작업 ID
        output_format: 출력 형식 ('markdown' 또는 'html')
        
    Returns:
        모델 리포트 문자열
    """
    try:
        # 모델 정보 로드
        from services.model_info import get_model_info
        model_info = get_model_info()
        
        # 학습 로그 파일 읽기
        log_file = f"data/training_logs/{job_id}.json"
        
        if not os.path.exists(log_file):
            return "학습 로그를 찾을 수 없습니다."
            
        with open(log_file, "r") as f:
            log_data = json.load(f)
        
        # 마크다운 형식의 리포트 생성
        if output_format == 'markdown':
            report = f"""# LSTM 모델 학습 리포트

## 모델 정보
- **아키텍처**: {model_info.get('architecture', 'LSTM')}
- **은닉층 크기**: {model_info.get('hidden_size', 'N/A')}
- **레이어 수**: {model_info.get('num_layers', 'N/A')}
- **양방향 여부**: {model_info.get('bidirectional', 'N/A')}
- **드롭아웃 비율**: {model_info.get('dropout', 'N/A')}

## 학습 정보
- **작업 ID**: {job_id}
- **상태**: {log_data.get('status', 'N/A')}
- **시작 시간**: {log_data.get('timestamp', 'N/A')}
- **학습 완료 시간**: {model_info.get('last_updated', 'N/A')}
- **총 학습 시간**: {model_info.get('training_time', 'N/A')}

## 성능 지표
- **정확도**: {model_info.get('accuracy', 'N/A')}
- **정밀도**: {model_info.get('precision', 'N/A')}
- **재현율**: {model_info.get('recall', 'N/A')}
- **F1 점수**: {model_info.get('f1_score', 'N/A')}

## 학습 과정
마지막 에폭 학습 손실: {log_data.get('train_loss', 'N/A')}
마지막 에폭 검증 손실: {log_data.get('val_loss', 'N/A')}
"""
            return report
        
        # HTML 형식의 리포트 생성
        elif output_format == 'html':
            # 여기에 HTML 형식의 리포트 생성
            return "<html>...</html>"  # 실제 구현 필요
        
        else:
            return "지원하지 않는 출력 형식입니다."
        
    except Exception as e:
        print(f"모델 리포트 생성 중 오류 발생: {str(e)}")
        return f"모델 리포트 생성 중 오류 발생: {str(e)}"