class CreateCheckManagers < ActiveRecord::Migration
  def change
    create_join_table :check_routes, :check_points, table_name: :check_manager do |t|

      t.index :check_route_id
      t.index :check_point_id

    end
  end
end
