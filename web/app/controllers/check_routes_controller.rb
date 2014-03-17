class CheckRoutesController < ApplicationController
  before_action :set_check_route, only: [:show, :edit, :update, :destroy]

  # GET /check_routes
  # GET /check_routes.json
  def index
    @check_routes = CheckRoute.all
  end

  # GET /check_routes/1
  # GET /check_routes/1.json
  def show
    begin
      render template: 'check_routes/show',  status: :ok
    rescue Exception => e
      render json: {:message=> e.to_s}.to_json, status: :not_found
    end
  end

  # POST /check_routes
  # POST /check_routes.json
  def create
    begin
      @check_route = CheckRoute.create!(check_route_params)
      render template: 'check_routes/show', status: :created
    rescue Exception => e
      render json: {:message=> e.to_s}.to_json, status: :internal_server_error
    end
  end

  # PATCH/PUT /check_routes/1
  # PATCH/PUT /check_routes/1.json
  def update
    respond_to do |format|
      if @check_route.update(check_route_params)
        format.html { redirect_to @check_route, notice: 'Check route was successfully updated.' }
        format.json { head :no_content }
      else
        format.html { render action: 'edit' }
        format.json { render json: @check_route.errors, status: :unprocessable_entity }
      end
    end
  end

  # DELETE /check_routes/1
  # DELETE /check_routes/1.json
  def destroy
    @check_route.destroy
    respond_to do |format|
      format.html { redirect_to check_routes_url }
      format.json { head :no_content }
    end
  end

  private
    # Use callbacks to share common setup or constraints between actions.
    def set_check_route
      @check_route = CheckRoute.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def check_route_params
      params.select{|key,value| key.in?(CheckRoute.column_names())}
    end
end
