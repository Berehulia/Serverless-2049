all: deploy

test:
	@echo "running tests"
	python3 -m venv venv
	./venv/bin/pip install -r tests/requirements.txt
	./venv/bin/python -m pytest tests/unit -v --cov

build: test
	@echo "building source code"
	sam build --use-container

validate: build
	@echo "validating SAM template"
	sam validate

deploy: validate
	@echo "deploying necessary resources"
	sam deploy

deploy-clean:
	@echo "removing deployed resources"
	aws cloudformation delete-stack --stack-name python-scenario-01