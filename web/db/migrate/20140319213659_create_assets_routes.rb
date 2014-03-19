class CreateAssetsRoutes < ActiveRecord::Migration
  def change
    create_table :assets_check_routes do |t|
      t.belongs_to :check_route
      t.belongs_to :asset
    end
  end
end
