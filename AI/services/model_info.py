def get_model_info():
    """
    모델의 메타데이터를 반환합니다. 
    이후 모델의 정보가 변경되면 이 함수를 수정하여 
    새로운 정보를 반환하도록 합니다.
    """
    return {
        "model_name": "StockLSTM",
        "input_size": 1,
        "hidden_size": 50,
        "num_layers": 2,
        "dropout": 0.2,
        "description": "LSTM model for stock price prediction"
    }